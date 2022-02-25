package xyz.snwjas.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.lang.NonNull;
import xyz.snwjas.blog.model.PageResult;
import xyz.snwjas.blog.model.entity.CommentEntity;
import xyz.snwjas.blog.model.enums.CommentStatus;
import xyz.snwjas.blog.model.params.BasePageParam;
import xyz.snwjas.blog.model.params.CommentSearchParam;
import xyz.snwjas.blog.model.vo.CommentDetailVO;
import xyz.snwjas.blog.model.vo.CommentSimpleVO;

import java.util.List;

/**
 * Comment Service
 *
 * @author Myles Yang
 */
public interface CommentService {

	/**
	 * 获取所有的评论的数量
	 */
	int getCountByStatus(CommentStatus status);

	/**
	 * 获取博客的评论数量
	 */
	int getCountByBlogIdAndStatus(int blogId, CommentStatus status);

	/**
	 * 获取子评论数量
	 */
	int getCountByParentIdAndStatus(int parentId, CommentStatus status);

	/**
	 * 改变评论状态
	 */
	int changeStatus(int commentId, CommentStatus status);

	/**
	 * 批量改变评论状态
	 *
	 * @return 成功更新状态的数量
	 */
	int changeStatus(List<Integer> commentIds, CommentStatus status);

	/**
	 * 删除评论
	 */
	int deleteById(int commentId);

	/**
	 * 批量删除评论
	 *
	 * @return 成功删除的数量
	 */
	int deleteByIds(@NonNull List<Integer> commentIds);

	/**
	 * 根据博客id删除评论
	 */
	int deleteByBlogId(int blogId);

	/**
	 * 回复评论
	 *
	 * @return > 0 评论id
	 * 0 不允许评论
	 * -1 文章不存在
	 */
	int reply(CommentDetailVO vo);

	/**
	 * 根据条件搜索评论
	 */
	IPage<CommentEntity> pageBy(CommentSearchParam param);

	/**
	 * 分页获取前台某层评论
	 *
	 * @param parentId 为 0 为顶层
	 */
	IPage<CommentEntity> pageBy(int blogId, int parentId, BasePageParam param);

	/**
	 * 从顶层往下递归获取所有子评论
	 */
	IPage<CommentEntity> pageBy(int blogId, BasePageParam param);

	CommentSimpleVO covertToSimpleVO(CommentEntity commentEntity);

	CommentDetailVO covertToDetailVO(CommentEntity commentEntity);

	PageResult<CommentDetailVO> covertToDetailPageResult(IPage<CommentEntity> page);

	PageResult<CommentSimpleVO> covertToSimplePageResult(IPage<CommentEntity> page);

	/**
	 * 从顶层往下递归获取所有子评论
	 */
	PageResult<CommentSimpleVO> covertToSimplePageResultByRecursively(IPage<CommentEntity> page);

	List<CommentDetailVO> covertToListDetailVO(List<CommentEntity> commentEntityList);

	List<CommentSimpleVO> covertToListSimpleVO(List<CommentEntity> commentEntityList);

}
