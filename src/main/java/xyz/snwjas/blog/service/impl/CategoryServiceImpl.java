package xyz.snwjas.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import xyz.snwjas.blog.mapper.BlogMapper;
import xyz.snwjas.blog.mapper.CategoryMapper;
import xyz.snwjas.blog.model.PageResult;
import xyz.snwjas.blog.model.entity.BlogEntity;
import xyz.snwjas.blog.model.entity.CategoryEntity;
import xyz.snwjas.blog.model.params.BasePageParam;
import xyz.snwjas.blog.model.vo.CategoryVO;
import xyz.snwjas.blog.service.CategoryService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Category Service
 *
 * @author Myles Yang
 */
@Service
public class CategoryServiceImpl implements CategoryService {

	@Resource
	private CategoryMapper categoryMapper;

	@Resource
	private BlogMapper blogMapper;

	@Override
	public int getCount() {
		return categoryMapper.selectCount(null);
	}

	@Override
	public List<CategoryVO> listUsedCategory() {
		List<BlogEntity> blogEntityList = blogMapper.selectList(
				Wrappers.lambdaQuery(BlogEntity.class)
						.select(BlogEntity::getCategoryId)
		);

		Set<Integer> categoryIds = blogEntityList.stream()
				.map(BlogEntity::getCategoryId)
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());

		// selectBatchIds 不能为空
		if (categoryIds.isEmpty()) {
			return new ArrayList<>();
		}

		List<CategoryEntity> categoryEntityList = categoryMapper.selectBatchIds(categoryIds);

		return covertToListVO(categoryEntityList);
	}

	@Override
	public List<CategoryVO> listAllCategory() {
		List<CategoryEntity> categoryEntityList = categoryMapper.selectList(Wrappers.emptyWrapper());
		return covertToListVO(categoryEntityList);
	}

	@Override
	public IPage<CategoryEntity> pageBy(BasePageParam param) {
		Page<CategoryEntity> page = new Page<>(param.getCurrent(), param.getPageSize());
		return categoryMapper.selectPage(page, Wrappers.emptyWrapper());
	}

	@Override
	public CategoryVO getById(int categoryId) {
		CategoryEntity categoryEntity = categoryMapper.selectById(categoryId);
		return covertToVO(categoryEntity);
	}

	@Override
	public CategoryVO getByName(String name) {
		CategoryEntity categoryEntity = categoryMapper.selectOne(
				Wrappers.lambdaQuery(CategoryEntity.class)
						.eq(CategoryEntity::getName, name)
		);
		return new CategoryVO().convertFrom(categoryEntity);
	}

	@Override
	public int add(CategoryVO categoryVO) {
		// 分类名已存在
		CategoryVO byName = getByName(categoryVO.getName());
		if (Objects.nonNull(byName)) {
			return -byName.getId();
		}
		CategoryEntity categoryEntity = categoryVO.convertTo(new CategoryEntity());
		categoryMapper.insert(categoryEntity);
		return categoryEntity.getId();
	}

	@Override
	public int update(CategoryVO categoryVO) {
		return categoryMapper.update(null, getBlogUpdateWrapper(categoryVO));
	}

	@Override
	public int deleteById(int categoryId) {
		int i = categoryMapper.deleteById(categoryId);
		// 把使用了该分类的博客置为未分类
		if (i > 0) {
			blogMapper.update(null,
					Wrappers.lambdaUpdate(BlogEntity.class)
							.eq(BlogEntity::getCategoryId, categoryId)
							.set(BlogEntity::getCategoryId, 0)
			);
		}
		return i;
	}

	@Override
	public CategoryVO covertToVO(CategoryEntity categoryEntity) {
		return new CategoryVO().convertFrom(categoryEntity);
	}

	@Override
	public List<CategoryVO> covertToListVO(List<CategoryEntity> categoryEntityList) {
		return categoryEntityList.stream().parallel()
				.map(this::covertToVO)
				.collect(Collectors.toList());
	}

	@Override
	public PageResult<CategoryVO> covertToPageResult(IPage<CategoryEntity> categoryPage) {
		List<CategoryEntity> categoryEntityList = categoryPage.getRecords();
		List<CategoryVO> categoryVOList = covertToListVO(categoryEntityList);
		return new PageResult<>(categoryPage.getTotal(), categoryVOList);
	}


	// 获取分类更新条件
	private Wrapper<CategoryEntity> getBlogUpdateWrapper(CategoryVO categoryVO) {
		String name = categoryVO.getName();
		String description = categoryVO.getDescription();

		return Wrappers.lambdaUpdate(CategoryEntity.class)
				.eq(CategoryEntity::getId, categoryVO.getId())
				.set(StringUtils.hasText(name), CategoryEntity::getName, name)
				.set(StringUtils.hasText(description), CategoryEntity::getDescription, description)

				;
	}


}
