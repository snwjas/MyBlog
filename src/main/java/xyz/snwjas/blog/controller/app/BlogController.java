package xyz.snwjas.blog.controller.app;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import xyz.snwjas.blog.annotation.AccessLimit;
import xyz.snwjas.blog.annotation.TimeCost;
import xyz.snwjas.blog.constant.CacheKeyPrefix;
import xyz.snwjas.blog.constant.RS;
import xyz.snwjas.blog.model.entity.BlogEntity;
import xyz.snwjas.blog.model.enums.BlogStatus;
import xyz.snwjas.blog.model.params.BlogSearchParam;
import xyz.snwjas.blog.model.vo.BlogArchiveVO;
import xyz.snwjas.blog.model.vo.BlogDetailVO;
import xyz.snwjas.blog.model.vo.BlogSimpleVO;
import xyz.snwjas.blog.service.BlogService;
import xyz.snwjas.blog.model.PageResult;
import xyz.snwjas.blog.model.R;
import xyz.snwjas.blog.support.cache.MemoryCacheStore;
import xyz.snwjas.blog.utils.IPUtils;
import xyz.snwjas.blog.utils.RUtils;
import xyz.snwjas.blog.utils.URLUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Blog Controller
 *
 * @author Myles Yang
 */
@Validated
@RestController("AppBlogController")
@RequestMapping("/api/app/blog")
@Api(value = "前台博客控制器", tags = {"前台博客文章接口"})
public class BlogController {

	private static final int PAGE_SIZE = 10;

	@Autowired
	private BlogService blogService;

	@Autowired
	private MemoryCacheStore cache;

	@AccessLimit(maxCount = 2)
	@PostMapping("/list")
	@ApiOperation("条件获取博客文章列表")
	public R listBlogs(@RequestBody @Validated BlogSearchParam param) {

		param.setStatus(BlogStatus.PUBLISHED);
		param.setPageSize(PAGE_SIZE);

		IPage<BlogEntity> blogPage = blogService.pageBy(param);
		PageResult<BlogSimpleVO> pageResult = blogService.covertToPageResult(blogPage);

		return RUtils.success("博客文章列表", pageResult);
	}

	@AccessLimit(maxCount = 1)
	@GetMapping("/list/achievement")
	@ApiOperation("获取文章归档")
	public R listAchievement() {
		Map<String, List<BlogArchiveVO>> map = blogService.listArchive();

		return RUtils.success("文章归档", map);
	}

	@TimeCost
	@AccessLimit(maxCount = 2)
	@GetMapping("/content/{blogUrl}")
	@ApiOperation("获取博客文章内容")
	public R getBlog(HttpServletRequest request,
	                 @PathVariable("blogUrl") @NotBlank String blogUrl) {

		String url = URLUtils.decode(blogUrl);
		BlogDetailVO blogContent = blogService.getByUrl(url);

		if (Objects.nonNull(blogContent)) {
			blogContent.setOriginalContent("");

			// 统计博客访问人数
			String ipAddress = IPUtils.getIpAddress(request);
			// 缓存在会在 StatisticTask 中清除
			String key = CacheKeyPrefix.BLOG_VISIT_COUNT + blogContent.getId()
					+ CacheKeyPrefix.SEPARATOR + ipAddress;
			cache.setIfAbsent(key, null);
		}

		return RUtils.success("博客文章内容", blogContent);
	}

	@AccessLimit(maxCount = 1)
	@GetMapping("/like/{blogId}")
	@ApiOperation("点赞博客")
	public R like(@PathVariable("blogId") @Min(1) Integer blogId) {
		int like = blogService.like(blogId);
		if (like == 0) {
			return RUtils.fail(RS.ILLEGAL_PARAMETER);
		}
		if (like == -1) {
			return RUtils.success("取消点赞", like);
		}
		return RUtils.success("点赞成功", like);
	}

}
