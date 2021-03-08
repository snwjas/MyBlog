package xyz.snwjas.blog.service;

import xyz.snwjas.blog.model.entity.TagEntity;
import xyz.snwjas.blog.model.vo.TagVO;

import java.util.List;
import java.util.Set;

/**
 * Tag Service
 *
 * @author Myles Yang
 */
public interface TagService {

	/**
	 * 获取所有的博客标签的数量
	 */
	int getCount();

	/**
	 * 获取某博客的标签
	 */
	List<TagVO> listBlogUsed(int blogId);

	/**
	 * 获取所有标签
	 */
	List<TagVO> listAll();

	/**
	 * 获取已使用的标签
	 */
	List<TagVO> listUsed();

	/**
	 * 根据id获取单个标签
	 */
	TagVO getById(int tagId);

	/**
	 * 根据标签id获取已使用该标签的博客id集合
	 */
	Set<Integer> listBlogIds(int tagId);

	/**
	 * 根据名称获取单个标签
	 */
	TagVO getByName(String name);

	/**
	 * 添加标签
	 *
	 * @return >0：标签id；<0：已存在标签的id负值
	 */
	int add(TagVO tagVO);

	/**
	 * 添加博客标签关联表记录
	 */
	int addBlogTag(int blogId, int tagId);


	/**
	 * 更新标签
	 *
	 * @return 更新状态，>0 :更新成功
	 */
	int update(TagVO tagVO);

	/**
	 * 删除标签
	 */
	int deleteById(int tagId);

	/**
	 * 更新博客的标签
	 *
	 * @return 标签更新的数量
	 */
	int updateBlogUsed(int blogId, List<TagVO> tagVOList);

	/**
	 * 删除博客标签
	 */
	int deleteBlogUsed(int blogId);

	/**
	 * 删除博客标签关联表记录
	 */
	int deleteBlogTag(int blogId, int tagId);

	/**
	 * TagEntity 转换成 TagVO
	 */
	TagVO covertToTagVO(TagEntity tagEntity);

	/**
	 * List<TagEntity> 转换成 List<TagVO>
	 */
	List<TagVO> covertToTagVOList(List<TagEntity> tagEntityList);

}
