package xyz.snwjas.blog.controller.admin;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import xyz.snwjas.blog.model.base.ValidGroupType;
import xyz.snwjas.blog.model.vo.TagVO;
import xyz.snwjas.blog.service.TagService;
import xyz.snwjas.blog.model.R;
import xyz.snwjas.blog.utils.RUtils;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>
 * 标签 前端控制器
 * </p>
 *
 * @author Myles Yang
 */
@Validated
@RestController("AdminTagController")
@RequestMapping("/api/admin/blog/tag")
@Api(value = "后台标签控制器", tags = {"后台标签接口"})
public class TagController {

	@Autowired
	private TagService tagService;

	@GetMapping("/list")
	@ApiOperation("获取所有的博客标签")
	public R listAllTags() {
		List<TagVO> tagVOList = tagService.listAll();
		return RUtils.success("所有的博客标签", tagVOList);
	}

	@DeleteMapping("/delete/{tagId}")
	@ApiOperation("删除标签")
	public R delete(@PathVariable("tagId") @NotNull @Min(1) Integer tagId) {
		int i = tagService.deleteById(tagId);
		return RUtils.commonFailOrNot(i, "标签删除");
	}

	@PostMapping("/add")
	@ApiOperation("添加标签")
	public R add(@RequestBody @Validated({ValidGroupType.Save.class}) TagVO vo) {
		int i = tagService.add(vo);
		if (i > 0) {
			return RUtils.success("标签添加成功");
		}
		if (i < 0) {
			return RUtils.fail("标签名已存在");
		}
		return RUtils.fail("标签添加失败");
	}

	@PostMapping("/update")
	@ApiOperation("更新标签")
	public R update(@RequestBody @Validated({ValidGroupType.Update.class}) TagVO vo) {
		int i = tagService.update(vo);
		return RUtils.commonFailOrNot(i, "标签更新");
	}
}
