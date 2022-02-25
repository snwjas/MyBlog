package xyz.snwjas.blog.controller.app;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import xyz.snwjas.blog.annotation.AccessLimit;
import xyz.snwjas.blog.model.PageResult;
import xyz.snwjas.blog.model.R;
import xyz.snwjas.blog.model.entity.CommentEntity;
import xyz.snwjas.blog.model.enums.CommentStatus;
import xyz.snwjas.blog.model.params.BasePageParam;
import xyz.snwjas.blog.model.vo.CommentDetailVO;
import xyz.snwjas.blog.model.vo.CommentSimpleVO;
import xyz.snwjas.blog.service.CommentService;
import xyz.snwjas.blog.support.wordfilter.WordFilter;
import xyz.snwjas.blog.utils.RUtils;
import xyz.snwjas.blog.utils.StrUtils;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.HashMap;

/**
 * Comment Controller
 *
 * @author Myles Yang
 */
@Validated
@RestController("AppCommentController")
@RequestMapping("/api/app/comment")
@Api(value = "前台评论控制器", tags = {"前台评论接口"})
public class CommentController {

	private static final int PAGE_SIZE = 7;

	@Autowired
	private CommentService commentService;

	@Autowired
	private WordFilter wordFilter;

	@AccessLimit(maxCount = 5)
	@PostMapping("/list")
	@ApiOperation("获取评论列表")
	public R listComments(@RequestParam("bid") @NotNull @Min(1) Integer blogId,
	                      @RequestParam("pid") @NotNull @Min(0) Integer parentId,
	                      @RequestParam("cur") @NotNull @Min(1) Integer current) {

		BasePageParam param = new BasePageParam()
				.setCurrent(current)
				.setPageSize(PAGE_SIZE);
		IPage<CommentEntity> entityPage = commentService.pageBy(blogId, parentId, param);
		PageResult<CommentSimpleVO> pageResult = commentService.covertToSimplePageResult(entityPage);

		return RUtils.success("评论列表", pageResult);
	}

	@AccessLimit(maxCount = 5)
	@PostMapping("/listr")
	@ApiOperation("顶层往下递归获取所有评论")
	public R listComments(@RequestParam("bid") @NotNull @Min(1) Integer blogId,
	                      @RequestParam("cur") @NotNull @Min(1) Integer current) {

		BasePageParam param = new BasePageParam()
				.setCurrent(current)
				.setPageSize(PAGE_SIZE);
		IPage<CommentEntity> entityPage = commentService.pageBy(blogId, param);
		PageResult<CommentSimpleVO> pageResult = commentService.covertToSimplePageResultByRecursively(entityPage);

		// 获取评论总数
		int count = commentService.getCountByBlogIdAndStatus(blogId, CommentStatus.PUBLISHED);

		HashMap<String, Object> map = new HashMap<>(4);
		map.put("all", count);
		map.put("total", pageResult.getTotal());
		map.put("list", pageResult.getList());

		return RUtils.success("评论列表", map);
	}

	@AccessLimit(maxCount = 5)
	@PostMapping("/publish")
	@ApiOperation("发表评论")
	public R publishComment(@RequestBody @Validated(CommentSimpleVO.Guest.class) CommentDetailVO vo) {

		String author = StrUtils.removeBlank(vo.getAuthor());
		vo.setAuthor(wordFilter.replace(author));
		String content = StrUtils.removeBlank(vo.getContent());
		vo.setContent(wordFilter.replace(content));
		int i = commentService.reply(vo);
		return RUtils.commonFailOrNot(i, "评论发表");
	}

}
