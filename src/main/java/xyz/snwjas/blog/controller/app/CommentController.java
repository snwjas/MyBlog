package xyz.snwjas.blog.controller.app;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import xyz.snwjas.blog.annotation.AccessLimit;
import xyz.snwjas.blog.annotation.TimeCost;
import xyz.snwjas.blog.constant.RS;
import xyz.snwjas.blog.model.PageResult;
import xyz.snwjas.blog.model.R;
import xyz.snwjas.blog.model.entity.CommentEntity;
import xyz.snwjas.blog.model.params.BasePageParam;
import xyz.snwjas.blog.model.vo.CommentDetailVO;
import xyz.snwjas.blog.model.vo.CommentSimpleVO;
import xyz.snwjas.blog.service.CommentService;
import xyz.snwjas.blog.support.wordfilter.WordFilter;
import xyz.snwjas.blog.utils.RUtils;
import xyz.snwjas.blog.utils.StrUtils;

import javax.validation.constraints.NotBlank;

/**
 * Comment Controller
 *
 * @author Myles Yang
 */
@RestController("AppCommentController")
@RequestMapping("/api/app/comment")
@Api(value = "前台评论控制器", tags = {"前台评论接口"})
public class CommentController {

	private static final int PAGE_SIZE = 7;

	@Autowired
	private CommentService commentService;

	@Autowired
	private WordFilter wordFilter;

	@AccessLimit(maxCount = 1)
	@PostMapping("/list")
	@ApiOperation("获取评论列表")
	public R listComments(@RequestParam("bid") @NotBlank String blogId,
	                      @RequestParam("pid") @NotBlank String parentId,
	                      @RequestParam("cur") @NotBlank String current) {

		int bid = Integer.parseInt(blogId);
		int pid = Integer.parseInt(parentId);
		int cur = Integer.parseInt(current);

		if (bid < 1 || pid < 0 || cur < 1) {
			return RUtils.fail(RS.ILLEGAL_PARAMETER);
		}

		BasePageParam param = new BasePageParam()
				.setCurrent(cur)
				.setPageSize(PAGE_SIZE);
		IPage<CommentEntity> entityPage = commentService.pageBy(bid, pid, param);
		PageResult<CommentSimpleVO> pageResult = commentService.covertToSimplePageResult(entityPage);

		return RUtils.success("评论列表", pageResult);
	}

	@TimeCost
	@AccessLimit(maxCount = 1)
	@PostMapping("/publish")
	@ApiOperation("发表评论")
	public R publishComment(@RequestBody @Validated(CommentSimpleVO.Guest.class) CommentDetailVO vo) {

		String author = StrUtils.removeBlank(vo.getAuthor());
		if (wordFilter.include(author)) {
			return RUtils.fail("检测到您的昵称中存在敏感词汇");
		}

		String content = StrUtils.removeBlank(vo.getContent());
		if (wordFilter.include(content)) {
			return RUtils.fail("检测到您的评论内容中存在敏感词汇");
		}

		int i = commentService.reply(vo);
		return RUtils.commonFailOrNot(i, "评论发表");
	}

}
