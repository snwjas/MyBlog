package xyz.snwjas.blog.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import xyz.snwjas.blog.model.base.ValidGroupType;
import xyz.snwjas.blog.model.entity.BlogEntity;
import xyz.snwjas.blog.model.params.BlogSearchParam;
import xyz.snwjas.blog.model.vo.BlogDetailVO;
import xyz.snwjas.blog.model.vo.BlogSelectVO;
import xyz.snwjas.blog.model.vo.BlogSimpleVO;
import xyz.snwjas.blog.service.BlogService;
import xyz.snwjas.blog.model.PageResult;
import xyz.snwjas.blog.model.R;
import xyz.snwjas.blog.utils.RUtils;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.Objects;

/**
 * Article Controller
 *
 * @author Myles Yang
 */
@Validated
@RestController("AdminBlogController")
@RequestMapping("/api/admin/blog")
@Api(value = "后台博客文章控制器", tags = {"后台博客文章接口"})
public class BlogController {

	@Autowired
	private BlogService blogService;

	@PostMapping("/list")
	@ApiOperation("获取博客文章列表信息")
	public R listBlogs(@RequestBody @Valid BlogSearchParam param) {
		IPage<BlogEntity> blogEntityIPage = blogService.pageBy(param);
		PageResult<BlogSimpleVO> pageResult = blogService.covertToPageResult(blogEntityIPage);
		return RUtils.success("博客文章列表信息", pageResult);
	}

	@GetMapping("/list/all-titles")
	@ApiOperation("获取所有的文章标题")
	public R listAllTitle() {
		List<BlogSelectVO> list = blogService.listAllTitle();
		return RUtils.success("所有的文章标题", list);
	}

	@GetMapping("/get/{id}")
	@ApiOperation("获取博客文章信息")
	public R getBlog(@PathVariable("id") @Min(1) Integer blogId) {
		BlogDetailVO detailVO = blogService.getDetailById(blogId);
		if (Objects.isNull(detailVO)) {
			return RUtils.fail("无此博客文章信息");
		}
		return RUtils.success("博客文章信息", detailVO);
	}

	@PostMapping("/add")
	@ApiOperation("发表博客文章")
	public R addBlog(@RequestBody @Validated(ValidGroupType.Save.class) BlogDetailVO vo) {
		int i = blogService.add(vo);
		return RUtils.commonFailOrNot(i, "发表博客文章");
	}

	@PostMapping("/update")
	@ApiOperation("更新博客文章信息")
	public R updateBlog(@RequestBody @Validated(ValidGroupType.Update.class) BlogDetailVO vo) {
		if (blogService.isExist(vo.getId(), vo.getUrl())) {
			return RUtils.fail("博客路径已存在");
		}
		int i = blogService.update(vo);
		return RUtils.commonFailOrNot(i, "博客信息文章更新");
	}

	@DeleteMapping("/delete/{blogId}")
	@ApiOperation("删除博客文章")
	public R deleteBlog(@PathVariable("blogId") @Min(1) Integer blogId) {
		int i = blogService.deleteById(blogId);
		return RUtils.commonFailOrNot(i, "删除博客文章");
	}

}
