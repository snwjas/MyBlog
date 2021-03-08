package xyz.snwjas.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import xyz.snwjas.blog.model.entity.CategoryEntity;
import xyz.snwjas.blog.model.params.BasePageParam;
import xyz.snwjas.blog.model.vo.CategoryVO;
import xyz.snwjas.blog.model.PageResult;

import java.util.List;

/**
 * Category Service
 *
 * @author Myles Yang
 */
public interface CategoryService {

	/**
	 * 获取分类数量
	 */
	int getCount();

	/**
	 * 列出已使用的博客分类名
	 */
	List<CategoryVO> listUsedCategory();

	/**
	 * 获取所有分类
	 */
	List<CategoryVO> listAllCategory();

	/**
	 * 分页查询分类
	 */
	IPage<CategoryEntity> pageBy(BasePageParam param);

	/**
	 * 根据分类id回去分类信息
	 */
	CategoryVO getById(int categoryId);

	/**
	 * 根据分类名回去分类信息
	 */
	CategoryVO getByName(String name);

	/**
	 * 添加分类
	 *
	 * @return 分类id, >0:添加成功; <0:已存在分类的id负值
	 */
	int add(CategoryVO categoryVO);

	/**
	 * 更新分类信息
	 *
	 * @return >0:更新成功
	 */
	int update(CategoryVO categoryVO);

	/**
	 * 删除分类
	 */
	int deleteById(int categoryId);

	CategoryVO covertToVO(CategoryEntity categoryEntity);

	List<CategoryVO> covertToListVO(List<CategoryEntity> categoryEntityList);

	PageResult<CategoryVO> covertToPageResult(IPage<CategoryEntity> categoryPage);

}
