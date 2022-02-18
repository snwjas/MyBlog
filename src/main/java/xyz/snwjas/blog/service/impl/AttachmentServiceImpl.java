package xyz.snwjas.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import xyz.snwjas.blog.config.properties.MyBlogProperties;
import xyz.snwjas.blog.mapper.AttachmentMapper;
import xyz.snwjas.blog.model.PageResult;
import xyz.snwjas.blog.model.entity.AttachmentEntity;
import xyz.snwjas.blog.model.params.AttachmentSearchParam;
import xyz.snwjas.blog.model.vo.AttachmentVO;
import xyz.snwjas.blog.service.AttachmentService;
import xyz.snwjas.blog.utils.FileUtils;
import xyz.snwjas.blog.utils.LambdaTypeUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Attachment Service Implementation
 *
 * @author Myles Yang
 */
@Service
@Slf4j
public class AttachmentServiceImpl implements AttachmentService {

	@Resource
	private AttachmentMapper attachmentMapper;

	@Autowired
	private MyBlogProperties properties;

	@Qualifier("executor")
	@Autowired
	private ThreadPoolTaskExecutor executor;

	private static final String THUMBNAIL_SUFFIX = "-thumbnail";

	private static final int THUMBNAIL_WIDTH = 320;

	private static final int THUMBNAIL_HEIGHT = 180;

	/**
	 * 图片压缩质量，区间(0.0, 1.0)
	 */
	private static final double THUMBNAIL_QUALITY = 0.7;


	@Override
	public AttachmentVO add(MultipartFile file, String team) {
		String filename = file.getOriginalFilename();
		String contentType = file.getContentType();

		File originalFile = getFileSavedPath(filename);
		File thumbnailFile = null;
		try {
			// 保存原文件
			file.transferTo(originalFile);
			// 如果是图片，生成缩略图
			if (StringUtils.hasText(contentType) && contentType.startsWith("image")) {
				thumbnailFile = saveThumbnail(originalFile);
			}
		} catch (IOException e) {
			log.warn("文件保存失败", e.getCause());
			return null;
		}
		// 静态资源都是映射到 /static/ 访问路径下
		String visitPath = "/static/" + originalFile.getParentFile().getName() + "/";

		AttachmentEntity attachment = new AttachmentEntity();
		attachment.setName(filename)
				.setMediaType(contentType)
				.setSize(file.getSize())
				.setPath(visitPath + originalFile.getName())
				.setTeam(Objects.isNull(team) ? "" : team);

		if (Objects.nonNull(thumbnailFile)) {
			int[] pixel = FileUtils.getImagePixel(originalFile);
			attachment.setThumbPath(visitPath + thumbnailFile.getName())
					.setWidth(pixel[0])
					.setHeight(pixel[1]);
		}

		attachmentMapper.insert(attachment);
		return covertToVO(attachment);
	}

	@Override
	public int updateNameById(int attachmentId, String attachmentName) {
		return attachmentMapper.update(null,
				Wrappers.lambdaUpdate(AttachmentEntity.class)
						.eq(AttachmentEntity::getId, attachmentId)
						.set(AttachmentEntity::getName, attachmentName)
		);
	}

	@Override
	public int deleteById(int attachmentId) {
		AttachmentEntity attachment = attachmentMapper.selectById(attachmentId);
		if (Objects.isNull(attachment)) {
			return -1;
		}

		String[] paths = attachment.getPath().split("/");
		String[] thumbPaths = attachment.getThumbPath().split("/");

		String savePath = properties.getFileSavePath();
		// 删除文件
		executor.execute(() -> {
			FileUtils.deleteFile(savePath + File.separatorChar
					+ paths[2] + File.separatorChar + paths[3]);
			FileUtils.deleteFile(savePath + File.separatorChar
					+ thumbPaths[2] + File.separatorChar + thumbPaths[3]);
		});

		return attachmentMapper.deleteById(attachmentId);
	}

	@Override
	public int deleteById(List<Integer> attachmentIds) {
		if (CollectionUtils.isEmpty(attachmentIds)) {
			return 0;
		}
		Set<Integer> idSet = attachmentIds.stream()
				.filter(id -> Objects.nonNull(id) && id > 0)
				.collect(Collectors.toSet());
		int count = 0;
		for (Integer id : idSet) {
			int i = deleteById(id);
			if (i > 0) {
				count++;
			}
		}
		return count;
	}

	@Override
	public int updateTeam(List<Integer> attachmentIds, String team) {
		if (CollectionUtils.isEmpty(attachmentIds)) {
			return 0;
		}
		Set<Integer> idSet = attachmentIds.stream()
				.filter(id -> Objects.nonNull(id) && id > 0)
				.collect(Collectors.toSet());

		return attachmentMapper.update(null,
				Wrappers.lambdaUpdate(AttachmentEntity.class)
						.set(AttachmentEntity::getTeam, team)
						.in(AttachmentEntity::getId, idSet)
		);
	}

	@Override
	public List<String> listAllMediaTypes() {
		QueryWrapper<AttachmentEntity> wrapper = new QueryWrapper<AttachmentEntity>()
				.select("DISTINCT " + LambdaTypeUtils.getColumnName(AttachmentEntity::getMediaType));

		List<AttachmentEntity> attachmentEntityList = attachmentMapper.selectList(wrapper);

		return attachmentEntityList.stream()
				.map(AttachmentEntity::getMediaType)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
	}

	@Override
	public List<String> listAllTeams() {
		QueryWrapper<AttachmentEntity> wrapper = new QueryWrapper<AttachmentEntity>()
				.select("DISTINCT " + LambdaTypeUtils.getColumnName(AttachmentEntity::getTeam));

		List<AttachmentEntity> attachmentEntityList = attachmentMapper.selectList(wrapper);

		return attachmentEntityList.stream()
				.map(AttachmentEntity::getTeam)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
	}

	@Override
	public IPage<AttachmentEntity> pageBy(AttachmentSearchParam param) {
		Page<AttachmentEntity> page = new Page<>(param.getCurrent(), param.getPageSize());
		Wrapper<AttachmentEntity> wrapper = getAttachmentSearchWrapper(param);
		return attachmentMapper.selectPage(page, wrapper);
	}

	@Override
	public PageResult<AttachmentVO> covertToPageResult(IPage<AttachmentEntity> page) {
		List<AttachmentEntity> attachmentEntityList = page.getRecords();
		List<AttachmentVO> attachmentVOList = covertToListVO(attachmentEntityList);
		return new PageResult<>(page.getTotal(), attachmentVOList);
	}

	@Override
	public AttachmentVO covertToVO(AttachmentEntity attachmentEntity) {
		return new AttachmentVO().convertFrom(attachmentEntity);
	}

	@Override
	public List<AttachmentVO> covertToListVO(@NonNull List<AttachmentEntity> attachmentEntityList) {
		return attachmentEntityList.stream().parallel()
				.map(this::covertToVO)
				.collect(Collectors.toList());
	}

	// 获取搜索条件
	private Wrapper<AttachmentEntity> getAttachmentSearchWrapper(AttachmentSearchParam param) {
		String name = param.getName();
		String mediaType = param.getMediaType();
		String team = param.getTeam();
		String likeTeam = param.getLikeTeam();

		return Wrappers.lambdaQuery(AttachmentEntity.class)
				.like(!StringUtils.isEmpty(name), AttachmentEntity::getName, name)
				.like(Objects.isNull(team) && !StringUtils.isEmpty(likeTeam), AttachmentEntity::getTeam, likeTeam)
				.eq(Objects.nonNull(mediaType), AttachmentEntity::getMediaType, mediaType)
				.eq(Objects.nonNull(team) || "".equals(likeTeam), AttachmentEntity::getTeam, team)
				.orderByDesc(AttachmentEntity::getId)
				;
	}

	/**
	 * 获取文件完整保存路径（如 /home/files/file.md）
	 * 文件名命名：毫秒时间戳-UUID.后缀
	 *
	 * @param fileName 文件名（包含扩展名）
	 * @return {@link File}
	 */
	public File getFileSavedPath(String fileName) {
		// 保存的文件夹以年月命名
		String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
		Path dest = Paths.get(properties.getFileSavePath(), date).toAbsolutePath();

		File destFile = dest.toFile();
		if (!destFile.exists()) {
			destFile.mkdirs();
		}

		String timestamp = String.valueOf(Instant.now().toEpochMilli());
		String uuid = UUID.randomUUID().toString().replaceAll("-", "");

		String[] nameExt = FileUtils.getFileNameAndExt(fileName);

		String newFileName = timestamp + "-" + uuid + nameExt[1];

		return Paths.get(dest.toString(), newFileName).toFile();
	}

	/**
	 * 保存缩略图
	 *
	 * @param file 原文件路径
	 * @return 缩略图路径
	 */
	public File saveThumbnail(File file) throws IOException {
		String thumbnailFileName = FileUtils.fileNameAppend(file.getName(), THUMBNAIL_SUFFIX);
		File thumbnailFile = new File(file.getParentFile(), thumbnailFileName);
		Thumbnails.of(file)
				.size(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT)
				.outputQuality(THUMBNAIL_QUALITY)
				.toFile(thumbnailFile);
		return thumbnailFile;
	}


}
