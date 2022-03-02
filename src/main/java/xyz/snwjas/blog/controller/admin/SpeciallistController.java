package xyz.snwjas.blog.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import xyz.snwjas.blog.model.PageResult;
import xyz.snwjas.blog.model.R;
import xyz.snwjas.blog.model.entity.SpeciallistEntity;
import xyz.snwjas.blog.model.enums.SpecialListType;
import xyz.snwjas.blog.model.params.ListParam;
import xyz.snwjas.blog.model.params.SpeciallistSearchParam;
import xyz.snwjas.blog.model.vo.SpeciallistVO;
import xyz.snwjas.blog.service.SpeciallistService;
import xyz.snwjas.blog.utils.RUtils;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Log Controller
 *
 * @author Myles Yang
 */
@Validated
@RestController("AdminSpeciallistController")
@RequestMapping("/api/admin/speciallist")
@Api(value = "后台特殊清单控制器", tags = {"后台特殊清单接口"})
public class SpeciallistController {

	@Autowired
	private SpeciallistService speciallistService;


	@PostMapping("/list")
	@ApiOperation("获取清单列表")
	public R listBlogs(@RequestBody @Valid SpeciallistSearchParam param) {
		IPage<SpeciallistEntity> page = speciallistService.pageBy(param);
		PageResult<SpeciallistVO> pageResult = speciallistService.covertToPageResult(page);
		return RUtils.success("清单列表", pageResult);
	}

	@DeleteMapping("/delete")
	@ApiOperation("批量删除清单")
	public R delete(@RequestBody @Validated ListParam<Integer> ids) {
		int i = speciallistService.deleteByIds(ids);
		return RUtils.commonFailOrNot(i, "批量删除清单");
	}

	@PostMapping("/add")
	@ApiOperation("批量导入清单")
	public R addBatch(@RequestParam("type") @NotNull SpecialListType type,
	                  @RequestParam("content") @Validated ListParam<String> contents) {
		boolean b = speciallistService.addBatch(type, contents);
		return RUtils.commonFailOrNot(b ? 1 : 0, "批量导入清单");
	}

	@GetMapping("/types")
	@ApiOperation("列出所有已枚举清单类型")
	public R listTypes() {
		List<SpeciallistVO> types = speciallistService.listEnumType();
		return RUtils.success("已枚举清单类型", types);
	}

	@GetMapping("/refresh-context/{type}")
	@ApiOperation("刷新上下文")
	public R refreshContext(@PathVariable("type") @NotNull SpecialListType type) {
		boolean succeed = speciallistService.refreshContext(type);
		return RUtils.commonFailOrNot(succeed ? 1 : 0, String.format("刷新上下文[%s]", type));
	}

}
