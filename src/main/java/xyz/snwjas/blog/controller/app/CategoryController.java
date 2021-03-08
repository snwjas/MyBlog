package xyz.snwjas.blog.controller.app;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.snwjas.blog.annotation.AccessLimit;
import xyz.snwjas.blog.model.enums.BlogStatus;
import xyz.snwjas.blog.model.vo.CategoryVO;
import xyz.snwjas.blog.service.BlogService;
import xyz.snwjas.blog.service.CategoryService;
import xyz.snwjas.blog.model.R;
import xyz.snwjas.blog.utils.RUtils;

import java.util.List;

/**
 * Category Controller
 *
 * @author Myles Yang
 */
@RestController("AppCategoryController")
@RequestMapping("/api/app/category")
@Api(value = "前台分类控制器", tags = {"前台分类接口"})
public class CategoryController {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private BlogService blogService;


	@AccessLimit(maxCount = 2)
	@GetMapping("/list")
	@ApiOperation("获取已使用的分类")
	public R listUsedCategory() {
		List<CategoryVO> usedCategoryList = categoryService.listUsedCategory();
		// 获取每个分类下的文章数目
		for (CategoryVO vo : usedCategoryList) {
			int count = blogService.getCountByCategoryId(vo.getId(), BlogStatus.PUBLISHED);
			vo.setBlogCount(count);
		}
		return RUtils.success("分类列表", usedCategoryList);
	}

}
