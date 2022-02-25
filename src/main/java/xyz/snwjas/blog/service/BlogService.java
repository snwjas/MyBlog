package xyz.snwjas.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import xyz.snwjas.blog.model.entity.BlogEntity;
import xyz.snwjas.blog.model.enums.BlogStatus;
import xyz.snwjas.blog.model.params.BlogSearchParam;
import xyz.snwjas.blog.model.vo.BlogArchiveVO;
import xyz.snwjas.blog.model.vo.BlogDetailVO;
import xyz.snwjas.blog.model.vo.BlogSelectVO;
import xyz.snwjas.blog.model.vo.BlogSimpleVO;
import xyz.snwjas.blog.model.PageResult;

import java.util.List;
import java.util.Map;

/**
 * Blog Service
 *
 * @author Myles Yang
 */
public interface BlogService {

	/**
	 * 获取已发表的博客数量
	 *
	 * @param status 为null则查询全部数量
	 */
	int getCount(BlogStatus status);

	/**
	 * 根据分类获取博客数量
	 *
	 * @param status 为null则查询对应分类的全部数量
	 */
	int getCountByCategoryId(int categoryId, BlogStatus status);

	/**
	 * 获取博客的总访问量
	 */
	int getTotalVisits();

	/**
	 * 根据条件获取博客分页信息(不包括originalContent与formatContent)
	 * 默认升序分页
	 */
	IPage<BlogEntity> pageBy(BlogSearchParam param);

	/**
	 * 列出所有文章标题及其Id
	 */
	List<BlogSelectVO> listAllTitle();

	/**
	 * 列出博客归档,返回对应年份的归档
	 */
	Map<String, List<BlogArchiveVO>> listArchive();

	/**
	 * 根据博客id获取博客的详细信息
	 */
	BlogDetailVO getDetailById(int blogId);

	/**
	 * 根据博客id获取博客的简单信息(不查询博客内容)
	 */
	BlogSimpleVO getSimpleById(int blogId);

	/**
	 * 根据博客url获取博客的详细信息
	 */
	BlogDetailVO getByUrl(String blogUrl);

	/**
	 * 更新博客信息
	 */
	int update(BlogDetailVO vo);

	/**
	 * 删除博客
	 */
	int deleteById(int blogId);

	/**
	 * 添加博客
	 */
	int add(BlogDetailVO vo);

	/**
	 * 点赞博客
	 *
	 * @return -1 取消点赞， 1 点赞成功，0 博客不存在
	 */
	int like(int blogId);

	/**
	 * 博客是否可以评论
	 */
	boolean canComment(int blogId);

	/**
	 * 根据博客id判断博客是否存在
	 */
	boolean isExist(int blogId);

	/**
	 * 根据博客路径判断博客是否存在
	 */
	boolean isExist(String url);

	/**
	 * 根据博客路径判断博客是否存在
	 */
	boolean isExist(int blogId, String url);

	/**
	 * BlogEntity to BlogSimpleVO
	 */
	BlogSimpleVO covertToSimpleVO(BlogEntity blogEntity);

	/**
	 * BlogEntity to BlogDetailVO
	 */
	BlogDetailVO covertToDetailVO(BlogEntity blogEntity);

	/**
	 * List<BlogEntity> to List<BlogSimpleVO>
	 */
	List<BlogSimpleVO> covertToListSimpleVO(List<BlogEntity> blogEntityList);

	/**
	 * IPage<BlogEntity> to PageResult<BlogSimpleVO>
	 */
	PageResult<BlogSimpleVO> covertToPageResult(IPage<BlogEntity> blogPage);


}
