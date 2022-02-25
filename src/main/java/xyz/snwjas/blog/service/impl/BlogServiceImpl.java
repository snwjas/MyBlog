package xyz.snwjas.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import xyz.snwjas.blog.annotation.ActionRecord;
import xyz.snwjas.blog.constant.CacheKeyPrefix;
import xyz.snwjas.blog.mapper.BlogMapper;
import xyz.snwjas.blog.model.PageResult;
import xyz.snwjas.blog.model.entity.BlogEntity;
import xyz.snwjas.blog.model.enums.BlogCommentAllowStatus;
import xyz.snwjas.blog.model.enums.BlogStatus;
import xyz.snwjas.blog.model.enums.CommentStatus;
import xyz.snwjas.blog.model.enums.LogType;
import xyz.snwjas.blog.model.params.BlogSearchParam;
import xyz.snwjas.blog.model.vo.*;
import xyz.snwjas.blog.service.BlogService;
import xyz.snwjas.blog.service.CategoryService;
import xyz.snwjas.blog.service.CommentService;
import xyz.snwjas.blog.service.TagService;
import xyz.snwjas.blog.support.cache.MemoryCacheStore;
import xyz.snwjas.blog.utils.IPUtils;
import xyz.snwjas.blog.utils.LambdaTypeUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Blog Service
 *
 * @author Myles Yang
 */
@Service
public class BlogServiceImpl implements BlogService {

	@Resource
	private BlogMapper blogMapper;

	@Autowired
	private TagService tagService;

	@Autowired
	private CommentService commentService;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private MemoryCacheStore cache;

	@Override
	public int getCount(BlogStatus status) {
		return blogMapper.selectCount(Wrappers.lambdaQuery(BlogEntity.class)
				.eq(Objects.nonNull(status), BlogEntity::getStatus, status)
		);
	}

	@Override
	public int getCountByCategoryId(int categoryId, BlogStatus status) {
		return blogMapper.selectCount(Wrappers.lambdaQuery(BlogEntity.class)
				.eq(BlogEntity::getCategoryId, categoryId)
				.eq(Objects.nonNull(status), BlogEntity::getStatus, status)
		);
	}

	@Override
	public int getTotalVisits() {
		LambdaQueryWrapper<BlogEntity> wrapper = Wrappers.lambdaQuery(BlogEntity.class)
				.eq(BlogEntity::getStatus, BlogStatus.PUBLISHED);
		return blogMapper.sum(LambdaTypeUtils.getColumnName(BlogEntity::getVisits), wrapper);
	}

	@Override
	public IPage<BlogEntity> pageBy(BlogSearchParam param) {
		Page<BlogEntity> page = new Page<>(param.getCurrent(), param.getPageSize());
		Wrapper<BlogEntity> wrapper = getSearchWrapper(param);
		return blogMapper.selectPage(page, wrapper);
	}

	@Override
	public List<BlogSelectVO> listAllTitle() {
		List<BlogEntity> entityList = blogMapper.selectList(
				Wrappers.lambdaQuery(BlogEntity.class)
						.select(BlogEntity::getId, BlogEntity::getTitle)
		);
		return entityList.stream()
				.map(e -> new BlogSelectVO().<BlogSelectVO>convertFrom(e))
				.collect(Collectors.toList());
	}

	@Override
	public Map<String, List<BlogArchiveVO>> listArchive() {
		List<BlogEntity> blogEntityList = blogMapper.selectList(
				Wrappers.lambdaQuery(BlogEntity.class)
						.select(BlogEntity::getId, BlogEntity::getTitle, BlogEntity::getUrl
								, BlogEntity::getCreateTime, BlogEntity::getUpdateTime)
						.eq(BlogEntity::getStatus, BlogStatus.PUBLISHED)
		);
		// 年份为键，倒序排列
		Map<String, List<BlogArchiveVO>> map = new TreeMap<>(Comparator.reverseOrder());
		for (BlogEntity blogEntity : blogEntityList) {
			BlogArchiveVO baVO = new BlogArchiveVO().convertFrom(blogEntity);
			// 年份为键
			LocalDateTime createTime = blogEntity.getCreateTime();
			String year = DateTimeFormatter.ofPattern("yyyy").format(createTime);

			List<BlogArchiveVO> voList = map.get(year);
			if (Objects.isNull(voList)) {
				List<BlogArchiveVO> list = new ArrayList<>();
				list.add(baVO);
				map.put(year, list);
			} else {
				voList.add(baVO);
			}
		}
		return map;
	}

	@Override
	public BlogDetailVO getDetailById(int blogId) {
		BlogEntity blogEntity = blogMapper.selectById(blogId);
		return covertToDetailVO(blogEntity);
	}

	@Override
	public BlogSimpleVO getSimpleById(int blogId) {
		BlogEntity blogEntity = blogMapper.selectOne(
				Wrappers.lambdaQuery(BlogEntity.class)
						.select(tfi -> !tfi.getColumn().equals(LambdaTypeUtils.getColumnName(BlogEntity::getOriginalContent))
								&& !tfi.getColumn().equals(LambdaTypeUtils.getColumnName(BlogEntity::getFormatContent)))
						.eq(BlogEntity::getId, blogId)
		);
		return covertToSimpleVO(blogEntity);
	}

	@Override
	public BlogDetailVO getByUrl(String blogUrl) {
		BlogEntity blogEntity = blogMapper.selectOne(
				Wrappers.lambdaQuery(BlogEntity.class)
						.eq(BlogEntity::getUrl, blogUrl)
		);
		return covertToDetailVO(blogEntity);
	}

	@ActionRecord(content = "#vo.title", type = LogType.BLOG_EDITED, condition = "#ret > 0")
	@Override
	public int update(BlogDetailVO vo) {
		// 更新博客标签
		if (Objects.nonNull(vo.getTags())) {
			tagService.updateBlogUsed(vo.getId(), vo.getTags());
		}
		return blogMapper.update(null, getUpdateWrapper(vo));
	}

	@ActionRecord(content = "'ID：'+#blogId", type = LogType.BLOG_DELETED, condition = "#ret > 0")
	@Override
	public int deleteById(int blogId) {
		// 删除评论
		commentService.deleteByBlogId(blogId);
		return blogMapper.deleteById(blogId);
	}

	@ActionRecord(content = "#vo.title", type = LogType.BLOG_PUBLISHED)
	@Override
	public int add(BlogDetailVO vo) {
		BlogEntity blogEntity = vo.convertTo(new BlogEntity());
		if (!StringUtils.hasText(blogEntity.getTitle())) {
			blogEntity.setTitle(LocalDateTime.now().toString());
		}
		if (!StringUtils.hasText(blogEntity.getSummary())) {
			blogEntity.setSummary("");
		}
		if (Objects.isNull(blogEntity.getStatus())) {
			blogEntity.setStatus(BlogStatus.PUBLISHED);
		}
		// 设置url
		String url = StringUtils.hasText(blogEntity.getUrl())
				? blogEntity.getUrl()
				: blogEntity.getTitle();
		if (isExist(url)) {
			// 数据库长度 255
			String newUrl = System.currentTimeMillis() + "-" + url;
			url = newUrl.substring(0, Math.min(newUrl.length(), 255));
		}
		blogEntity.setUrl(url);
		// 设置分类
		CategoryVO category = vo.getCategory();
		Integer cid = Optional.ofNullable(category)
				.map(CategoryVO::getId)
				.filter(e -> e > 0)
				.orElse(0);
		blogEntity.setCategoryId(cid);
		// 执行插入
		blogMapper.insert(blogEntity);
		// 新增记录注解id
		Integer blogId = blogEntity.getId();
		// 更新博客标签
		tagService.updateBlogUsed(blogId, vo.getTags());

		return blogId;
	}

	/**
	 * 添加或更新博客信息时，对信息的检验修正
	 */
	private BlogDetailVO checkBlogData(BlogDetailVO vo) {
		return null;
	}

	@Override
	public int like(int blogId) {
		BlogEntity blog = blogMapper.selectOne(
				Wrappers.lambdaQuery(BlogEntity.class)
						.select(BlogEntity::getLikes)
						.eq(BlogEntity::getId, blogId)
		);
		if (Objects.isNull(blog)) {
			return 0;
		}

		Integer likes = blog.getLikes();
		likes = Objects.isNull(likes) || likes < 1
				? 0
				: likes;

		HttpServletRequest request = IPUtils.getRequest();
		String ipAddress = IPUtils.getIpAddress(request);
		String key = CacheKeyPrefix.BLOG_LIKE + ipAddress;
		boolean containsKey = cache.containsKey(key);
		// 已经点赞，取消
		if (containsKey) {
			cache.delete(key);
			blogMapper.update(null,
					Wrappers.lambdaUpdate(BlogEntity.class)
							.eq(BlogEntity::getId, blogId)
							.set(BlogEntity::getLikes, likes - 1)
			);
			return -1;
		}
		// 未点赞
		cache.set(key, null, 86400);
		blogMapper.update(null,
				Wrappers.lambdaUpdate(BlogEntity.class)
						.eq(BlogEntity::getId, blogId)
						.set(BlogEntity::getLikes, likes + 1)
		);
		return 1;
	}

	@Override
	public boolean canComment(int blogId) {
		BlogEntity entity = blogMapper.selectOne(
				Wrappers.lambdaQuery(BlogEntity.class)
						.select(BlogEntity::getAllowComment)
						.eq(BlogEntity::getId, blogId)
		);
		return Objects.nonNull(entity)
				&& Objects.nonNull(entity.getAllowComment())
				&& entity.getAllowComment() != BlogCommentAllowStatus.UNALLOWED;
	}

	@Override
	public boolean isExist(int blogId) {
		BlogEntity entity = blogMapper.selectOne(
				Wrappers.lambdaQuery(BlogEntity.class)
						.select(BlogEntity::getId)
						.eq(BlogEntity::getId, blogId)
		);
		return Objects.nonNull(entity);
	}

	@Override
	public boolean isExist(String url) {
		BlogEntity entity = blogMapper.selectOne(
				Wrappers.lambdaQuery(BlogEntity.class)
						.select(BlogEntity::getUrl)
						.eq(BlogEntity::getUrl, url)
		);
		return Objects.nonNull(entity);
	}

	@Override
	public boolean isExist(int blogId, String url) {
		BlogEntity entity = blogMapper.selectOne(
				Wrappers.lambdaQuery(BlogEntity.class)
						.select(BlogEntity::getUrl)
						.eq(BlogEntity::getUrl, url)
						.ne(BlogEntity::getId, blogId)
		);
		return Objects.nonNull(entity);
	}

	@Override
	public PageResult<BlogSimpleVO> covertToPageResult(IPage<BlogEntity> blogPage) {

		List<BlogSimpleVO> blogSimpleVOList = covertToListSimpleVO(blogPage.getRecords());

		return new PageResult<>(blogPage.getTotal(), blogSimpleVOList);
	}

	@Override
	public List<BlogSimpleVO> covertToListSimpleVO(List<BlogEntity> blogEntityList) {
		return blogEntityList.stream().parallel()
				.map(this::covertToSimpleVO)
				.collect(Collectors.toList());
	}

	@Override
	public BlogSimpleVO covertToSimpleVO(BlogEntity blogEntity) {

		BlogSimpleVO blogSimpleVO = new BlogSimpleVO().convertFrom(blogEntity);

		if (Objects.isNull(blogSimpleVO)) {
			return null;
		}

		Integer categoryId = blogEntity.getCategoryId();
		CategoryVO categoryVO = categoryService.getById(Objects.isNull(categoryId) ? 0 : categoryId);
		blogSimpleVO.setCategory(categoryVO);

		int commentCount = commentService.getCountByBlogIdAndStatus(blogEntity.getId(), CommentStatus.PUBLISHED);
		blogSimpleVO.setCommentCount(commentCount);

		List<TagVO> blogTagList = tagService.listBlogUsed(blogEntity.getId());
		blogSimpleVO.setTags(blogTagList);

		return blogSimpleVO;
	}

	@Override
	public BlogDetailVO covertToDetailVO(BlogEntity blogEntity) {
		BlogDetailVO blogDetailVO = new BlogDetailVO().convertFrom(blogEntity);

		if (Objects.isNull(blogDetailVO)) {
			return null;
		}

		Integer categoryId = blogEntity.getCategoryId();
		CategoryVO categoryVO = categoryService.getById(Objects.isNull(categoryId) ? 0 : categoryId);
		blogDetailVO.setCategory(categoryVO);

		int commentCount = commentService.getCountByBlogIdAndStatus(blogEntity.getId(), CommentStatus.PUBLISHED);
		blogDetailVO.setCommentCount(commentCount);

		List<TagVO> blogTagList = tagService.listBlogUsed(blogEntity.getId());
		blogDetailVO.setTags(blogTagList);

		return blogDetailVO;
	}

	// 获取搜索条件
	private Wrapper<BlogEntity> getSearchWrapper(BlogSearchParam param) {
		BlogStatus status = param.getStatus();
		Integer categoryId = param.getCategoryId();
		Integer tagId = param.getTagId();
		String title = param.getTitle();

		Set<Integer> blogIdSet = Objects.isNull(tagId)
				? Collections.emptySet()
				: tagService.listBlogIds(tagId);

		return Wrappers.lambdaQuery(BlogEntity.class)
				// 搜索不包括originalContent与formatContent
				.select(ti -> !ti.getColumn().equals(LambdaTypeUtils.getColumnName(BlogEntity::getOriginalContent))
						&& !ti.getColumn().equals(LambdaTypeUtils.getColumnName(BlogEntity::getFormatContent)))
				.eq(Objects.nonNull(status), BlogEntity::getStatus, status)
				.eq(Objects.nonNull(categoryId), BlogEntity::getCategoryId, categoryId)
				.in(Objects.nonNull(tagId), BlogEntity::getId, blogIdSet)
				.like(!StringUtils.isEmpty(title), BlogEntity::getTitle, title)
				// 默认升序搜索
				.orderByDesc(BlogEntity::getTopRank)
				.orderByDesc(BlogEntity::getId);
	}

	// 获取博客更新条件
	private Wrapper<BlogEntity> getUpdateWrapper(BlogDetailVO bd) {
		Integer id = bd.getId();
		String title = bd.getTitle();
		String url = bd.getUrl();
		String summary = bd.getSummary();
		String thumbnail = bd.getThumbnail();
		BlogStatus status = bd.getStatus();
		CategoryVO category = bd.getCategory();
		BlogCommentAllowStatus allowComment = bd.getAllowComment();
		Integer topRank = bd.getTopRank();
		String originalContent = bd.getOriginalContent();
		String formatContent = bd.getFormatContent();

		// 是否更新分类
		boolean c = Objects.nonNull(category)
				&& Objects.nonNull(category.getId())
				&& category.getId() > 0;

		return Wrappers.lambdaUpdate(BlogEntity.class)
				.eq(BlogEntity::getId, id)
				.set(Objects.nonNull(status), BlogEntity::getStatus, status)
				.set(Objects.nonNull(allowComment), BlogEntity::getAllowComment, allowComment)
				.set(Objects.nonNull(topRank), BlogEntity::getTopRank, topRank)
				.set(c, BlogEntity::getCategoryId, c ? category.getId() : 0)
				.set(StringUtils.hasText(title), BlogEntity::getTitle, title)
				.set(!StringUtils.isEmpty(url), BlogEntity::getUrl, url)
				.set(!StringUtils.isEmpty(summary), BlogEntity::getSummary, summary)
				.set(!StringUtils.isEmpty(thumbnail), BlogEntity::getThumbnail, thumbnail)
				.set(!StringUtils.isEmpty(originalContent), BlogEntity::getOriginalContent, originalContent)
				.set(!StringUtils.isEmpty(formatContent), BlogEntity::getFormatContent, formatContent)

				;
	}


}



