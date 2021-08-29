package xyz.snwjas.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import xyz.snwjas.blog.mapper.CommentMapper;
import xyz.snwjas.blog.model.PageResult;
import xyz.snwjas.blog.model.entity.CommentEntity;
import xyz.snwjas.blog.model.enums.BlogCommentAllowStatus;
import xyz.snwjas.blog.model.enums.CommentStatus;
import xyz.snwjas.blog.model.params.BasePageParam;
import xyz.snwjas.blog.model.params.CommentSearchParam;
import xyz.snwjas.blog.model.vo.BlogSimpleVO;
import xyz.snwjas.blog.model.vo.CommentDetailVO;
import xyz.snwjas.blog.model.vo.CommentSimpleVO;
import xyz.snwjas.blog.service.BlogService;
import xyz.snwjas.blog.service.CommentService;
import xyz.snwjas.blog.service.UserService;
import xyz.snwjas.blog.utils.IPUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Comment Service
 *
 * @author Myles Yang
 */
@Service
public class CommentServiceImpl implements CommentService {

	@Resource
	private CommentMapper commentMapper;

	@Autowired
	private UserService userService;

	@Autowired
	private BlogService blogService;

	@Override
	public int getCountByStatus(CommentStatus status) {
		return commentMapper.selectCount(
				Wrappers.lambdaQuery(CommentEntity.class)
						.eq(CommentEntity::getStatus, status)
		);
	}

	@Override
	public int getCountByBlogIdAndStatus(int blogId, CommentStatus status) {
		return commentMapper.selectCount(
				Wrappers.lambdaQuery(CommentEntity.class)
						.eq(CommentEntity::getBlogId, blogId)
						.eq(CommentEntity::getStatus, status)
		);
	}

	@Override
	public int getCountByParentId(int parentId) {
		return commentMapper.selectCount(
				Wrappers.lambdaQuery(CommentEntity.class)
						.eq(CommentEntity::getParentId, parentId)
		);
	}

	@Override
	public int changeStatus(int commentId, CommentStatus status) {
		return commentMapper.update(null,
				Wrappers.lambdaUpdate(CommentEntity.class)
						.eq(CommentEntity::getId, commentId)
						.set(CommentEntity::getStatus, status)
		);
	}

	@Override
	public int changeStatus(List<Integer> commentIds, CommentStatus status) {
		if (CollectionUtils.isEmpty(commentIds)) {
			return 0;
		}
		Set<Integer> idSet = commentIds.stream()
				.filter(id -> Objects.nonNull(id) && id > 0)
				.collect(Collectors.toSet());
		int count = 0;
		for (Integer commentId : idSet) {
			int i = changeStatus(commentId, status);
			if (i > 0) {
				count++;
			}
		}
		return count;
	}

	@Override
	public int deleteById(int commentId) {
		return commentMapper.deleteById(commentId);
	}

	@Override
	public int deleteByIds(@NonNull List<Integer> commentIds) {
		Set<Integer> idSet = commentIds.stream()
				.filter(id -> Objects.nonNull(id) && id > 0)
				.collect(Collectors.toSet());
		if (idSet.isEmpty()) {
			return 0;
		}
		return commentMapper.deleteBatchIds(idSet);
	}

	@Override
	public int deleteByBlogId(int blogId) {
		return commentMapper.delete(
				Wrappers.lambdaQuery(CommentEntity.class)
						.eq(CommentEntity::getBlogId, blogId)
		);
	}

	@Override
	public int reply(CommentDetailVO vo) {
		BlogSimpleVO blog = blogService.getSimpleById(vo.getBlogId());
		// 文章不存在
		if (Objects.isNull(blog)) {
			return -1;
		}

		// 不允许评论
		BlogCommentAllowStatus allowComment = blog.getAllowComment();
		if (Objects.isNull(allowComment) || allowComment == BlogCommentAllowStatus.UNALLOWED) {
			return 0;
		}

		CommentEntity commentEntity = vo.convertTo(new CommentEntity());
		commentEntity.setBlogId(vo.getBlogId());

		Integer parentId = vo.getParentId();
		commentEntity.setParentId(Objects.isNull(parentId) || parentId < 0 ? 0 : parentId);

		Authentication auth = userService.getAuth();
		if (Objects.isNull(auth)) { // 非管理员
			commentEntity.setIsAdmin(false);

			CommentStatus commentStatus =
					allowComment == BlogCommentAllowStatus.ALLOWED_AUDITING
							? CommentStatus.AUDITING
							: CommentStatus.PUBLISHED; // 不用审核
			commentEntity.setStatus(commentStatus);

			HttpServletRequest request = IPUtils.getRequest();

			String userAgent = request.getHeader("User-Agent");
			// 不做长度检查，可能会超过数据库字段长度
			userAgent = Objects.isNull(userAgent) ? "" : userAgent;
			commentEntity.setUserAgent(userAgent);

			String ipAddress = IPUtils.getIpAddress(request);
			commentEntity.setIpAddress(IPUtils.ipv4ToInt(ipAddress));
		} else {
			commentEntity.setIsAdmin(true);
			commentEntity.setStatus(CommentStatus.PUBLISHED);
		}

		commentMapper.insert(commentEntity);

		return commentEntity.getId();
	}

	@Override
	public IPage<CommentEntity> pageBy(CommentSearchParam param) {
		Page<CommentEntity> page = new Page<>(param.getCurrent(), param.getPageSize());
		Wrapper<CommentEntity> wrapper = getCommonSearchWrapper(param);
		return commentMapper.selectPage(page, wrapper);
	}

	// 分页获取前台顶层评论
	@Override
	public IPage<CommentEntity> pageBy(int blogId, int parentId, BasePageParam param) {
		Page<CommentEntity> page = new Page<>(param.getCurrent(), param.getPageSize());
		Wrapper<CommentEntity> wrapper = Wrappers.lambdaQuery(CommentEntity.class)
				// 顶层评论的父id为0
				.eq(CommentEntity::getParentId, parentId)
				.eq(CommentEntity::getBlogId, blogId)
				.orderByDesc(CommentEntity::getId);
		return commentMapper.selectPage(page, wrapper);
	}

	@Override
	public CommentSimpleVO covertToSimpleVO(CommentEntity commentEntity) {
		CommentSimpleVO simpleVO = new CommentSimpleVO().convertFrom(commentEntity);

		// 子评论数量
		int count = getCountByParentId(simpleVO.getId());
		simpleVO.setChildrenCount(count);

		return simpleVO;
	}

	@Override
	public CommentDetailVO covertToDetailVO(CommentEntity commentEntity) {
		CommentDetailVO detailVO = new CommentDetailVO().convertFrom(commentEntity);
		// 转换 ip 地址格式
		Integer ipAddress = commentEntity.getIpAddress();
		if (Objects.nonNull(ipAddress)) {
			String ipv4 = IPUtils.intToIpv4(ipAddress);
			detailVO.setIpAddress(ipv4);
		}
		// 子评论数量
		int count = getCountByParentId(detailVO.getId());
		detailVO.setChildrenCount(count);

		// 获取博客标题和url
		BlogSimpleVO blog = blogService.getSimpleById(commentEntity.getBlogId());
		if (Objects.nonNull(blog)) {
			detailVO.setBlogTitle(blog.getTitle());
			detailVO.setBlogUrl(blog.getUrl());
		}

		return detailVO;
	}

	@Override
	public PageResult<CommentDetailVO> covertToDetailPageResult(IPage<CommentEntity> page) {
		List<CommentEntity> records = page.getRecords();
		List<CommentDetailVO> detailVOList = covertToListDetailVO(records);
		return new PageResult<>(page.getTotal(), detailVOList);
	}

	@Override
	public PageResult<CommentSimpleVO> covertToSimplePageResult(IPage<CommentEntity> page) {
		List<CommentEntity> records = page.getRecords();
		List<CommentSimpleVO> simpleVOList = covertToListSimpleVO(records);
		return new PageResult<>(page.getTotal(), simpleVOList);
	}

	@Override
	public List<CommentDetailVO> covertToListDetailVO(List<CommentEntity> commentEntityList) {
		return commentEntityList.stream().parallel()
				.map(this::covertToDetailVO)
				.collect(Collectors.toList());
	}

	@Override
	public List<CommentSimpleVO> covertToListSimpleVO(List<CommentEntity> commentEntityList) {
		return commentEntityList.stream().parallel()
				.map(this::covertToSimpleVO)
				.collect(Collectors.toList());
	}

	// 获取搜索条件
	private Wrapper<CommentEntity> getCommonSearchWrapper(CommentSearchParam param) {
		String author = param.getAuthor();
		String content = param.getContent();
		String email = param.getEmail();
		String keyword = param.getKeyword();

		Integer blogId = param.getBlogId();
		CommentStatus status = param.getStatus();

		boolean hasKeyword = StringUtils.hasText(keyword);

		return Wrappers.lambdaQuery(CommentEntity.class)
				// like has keyword
				.like(hasKeyword, CommentEntity::getAuthor, keyword)
				.like(hasKeyword, CommentEntity::getContent, keyword)
				.like(hasKeyword, CommentEntity::getEmail, keyword)
				// like has not keyword
				.like(!hasKeyword && StringUtils.hasText(author), CommentEntity::getAuthor, author)
				.like(!hasKeyword && StringUtils.hasText(content), CommentEntity::getContent, content)
				.like(!hasKeyword && StringUtils.hasText(email), CommentEntity::getEmail, email)
				.eq(Objects.nonNull(status), CommentEntity::getStatus, status)
				.eq(Objects.nonNull(blogId), CommentEntity::getBlogId, blogId)
				;
	}


}
