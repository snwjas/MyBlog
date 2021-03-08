package xyz.snwjas.blog.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import xyz.snwjas.blog.model.entity.CommentEntity;
import xyz.snwjas.blog.model.enums.CommentStatus;
import xyz.snwjas.blog.model.params.CommentSearchParam;
import xyz.snwjas.blog.model.params.ListParam;
import xyz.snwjas.blog.model.vo.CommentDetailVO;
import xyz.snwjas.blog.model.vo.CommentSimpleVO;
import xyz.snwjas.blog.service.CommentService;
import xyz.snwjas.blog.model.PageResult;
import xyz.snwjas.blog.model.R;
import xyz.snwjas.blog.utils.RUtils;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Comment Controller
 *
 * @author Myles Yang
 */
@Validated
@RestController("AdminCommentController")
@RequestMapping("/api/admin/comment")
@Api(value = "后台评论控制器", tags = {"后台评论接口"})
public class CommentController {

	@Autowired
	private CommentService commentService;

	@PostMapping("/list")
	@ApiOperation("获取评论列表信息")
	public R list(@RequestBody @Valid CommentSearchParam param) {
		IPage<CommentEntity> blogEntityIPage = commentService.pageBy(param);
		PageResult<CommentDetailVO> pageResult = commentService.covertToDetailPageResult(blogEntityIPage);
		return RUtils.success("评论列表信息", pageResult);
	}

	@PostMapping("/update/status")
	@ApiOperation("更新评论状态")
	public R updateStatus(@RequestParam("id") @NotNull @Min(1) Integer id,
	                      @RequestParam("status") @NotNull CommentStatus status) {
		int i = commentService.changeStatus(id, status);
		return RUtils.commonFailOrNot(i, "更新评论状态");
	}

	@PostMapping("/update/status-batch")
	@ApiOperation("批量更新评论状态")
	public R updateStatusBatch(@RequestParam("ids") @Validated ListParam<Integer> ids,
	                           @RequestParam("status") @NotNull CommentStatus status) {
		int count = commentService.changeStatus(ids, status);
		return RUtils.success("成功更新了" + count + "条评论的状态");
	}

	@DeleteMapping("/delete/{commentId}")
	@ApiOperation("删除评论")
	public R delete(@PathVariable("commentId") @Min(1) Integer commentId) {
		int i = commentService.deleteById(commentId);
		return RUtils.commonFailOrNot(i, "删除评论");
	}

	@DeleteMapping("/delete")
	@ApiOperation("批量删除评论")
	public R delete(@RequestBody @Validated ListParam<Integer> commentIds) {
		int i = commentService.deleteByIds(commentIds);
		return RUtils.commonFailOrNot(i, "批量删除评论");
	}

	@PostMapping("/reply")
	@ApiOperation("回复评论")
	public R reply(@RequestBody @Validated(CommentSimpleVO.Admin.class) CommentDetailVO vo) {
		int i = commentService.reply(vo);
		return RUtils.commonFailOrNot(i, "回复评论");
	}


}
