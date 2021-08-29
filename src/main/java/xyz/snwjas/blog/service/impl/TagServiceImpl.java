package xyz.snwjas.blog.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import xyz.snwjas.blog.annotation.TimeCost;
import xyz.snwjas.blog.mapper.BlogTagMapper;
import xyz.snwjas.blog.mapper.TagMapper;
import xyz.snwjas.blog.model.entity.BlogTagEntity;
import xyz.snwjas.blog.model.entity.TagEntity;
import xyz.snwjas.blog.model.vo.TagVO;
import xyz.snwjas.blog.service.TagService;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Tag Service
 *
 * @author Myles Yang
 */
@Service
public class TagServiceImpl implements TagService {

	@Resource
	private TagMapper tagMapper;

	@Resource
	private BlogTagMapper blogTagMapper;

	@Override
	public int getCount() {
		return tagMapper.selectCount(null);
	}

	@Override
	public List<TagVO> listBlogUsed(int blogId) {

		List<BlogTagEntity> blogTagEntities = blogTagMapper.selectList(
				Wrappers.lambdaQuery(BlogTagEntity.class)
						.eq(BlogTagEntity::getBlogId, blogId)
		);

		if (blogTagEntities.isEmpty()) {
			return Collections.emptyList();
		}

		Set<Integer> tagIds = blogTagEntities.stream()
				.map(BlogTagEntity::getTagId)
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());

		List<TagEntity> tagEntityList = tagMapper.selectBatchIds(tagIds);

		return covertToTagVOList(tagEntityList);
	}

	@Override
	public List<TagVO> listAll() {
		List<TagEntity> tagEntityList = tagMapper.selectList(Wrappers.emptyWrapper());
		return covertToTagVOList(tagEntityList);
	}

	@Override
	public List<TagVO> listUsed() {
		List<BlogTagEntity> blogTagEntityList = blogTagMapper.selectList(
				Wrappers.lambdaQuery(BlogTagEntity.class)
						.select(BlogTagEntity::getTagId)
		);

		// selectBatchIds 参数集合不能为空
		if (CollectionUtils.isEmpty(blogTagEntityList)) {
			return Collections.emptyList();
		}

		Set<Integer> tagIdSet = blogTagEntityList.stream()
				.map(BlogTagEntity::getTagId)
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());

		List<TagEntity> tagEntityList = tagMapper.selectBatchIds(tagIdSet);
		return covertToTagVOList(tagEntityList);
	}

	@Override
	public TagVO getById(int tagId) {
		TagEntity tagEntity = tagMapper.selectOne(
				Wrappers.lambdaQuery(TagEntity.class)
						.eq(TagEntity::getId, tagId)
		);
		return new TagVO().convertFrom(tagEntity);
	}

	@Override
	public Set<Integer> listBlogIds(int tagId) {
		List<BlogTagEntity> blogTagEntityList = blogTagMapper.selectList(
				Wrappers.lambdaQuery(BlogTagEntity.class)
						.select(BlogTagEntity::getBlogId)
						.eq(BlogTagEntity::getTagId, tagId)
		);
		return blogTagEntityList.stream()
				.map(BlogTagEntity::getBlogId)
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());
	}

	@Override
	public TagVO getByName(String name) {
		TagEntity tagEntity = tagMapper.selectOne(
				Wrappers.lambdaQuery(TagEntity.class)
						.eq(TagEntity::getName, name)
		);
		return new TagVO().convertFrom(tagEntity);
	}

	@Override
	public int add(TagVO tagVO) {
		TagVO byName = getByName(tagVO.getName());
		// 标签已经存在
		if (Objects.nonNull(byName)) {
			return -byName.getId();
		}
		TagEntity tagEntity = tagVO.convertTo(new TagEntity());
		tagMapper.insert(tagEntity);
		return tagEntity.getId();
	}

	@Override
	public int addBlogTag(int blogId, int tagId) {
		BlogTagEntity blogTagEntity = new BlogTagEntity()
				.setBlogId(blogId)
				.setTagId(tagId);
		blogTagMapper.insert(blogTagEntity);
		return blogTagEntity.getId();
	}

	@Override
	public int update(TagVO tagVO) {
		return tagMapper.update(null,
				Wrappers.lambdaUpdate(TagEntity.class)
						.eq(TagEntity::getId, tagVO.getId())
						.set(TagEntity::getName, tagVO.getName())
		);
	}

	@Override
	public int deleteById(int tagId) {
		int i = tagMapper.deleteById(tagId);
		// 删除id后，删除博客使用的
		if (i > 0) {
			blogTagMapper.delete(
					Wrappers.lambdaQuery(BlogTagEntity.class)
							.eq(BlogTagEntity::getTagId, tagId)
			);
		}
		return i;
	}

	@TimeCost
	@Override
	public int updateBlogUsed(int blogId, @NonNull List<TagVO> tagVOList) {
		if (tagVOList.isEmpty()) {
			return deleteBlogUsed(blogId);
		}

		Set<Integer> oldTagIdSet = listBlogUsed(blogId).stream()
				.map(TagVO::getId)
				.collect(Collectors.toSet());

		Set<Integer> newTagIdSet = tagVOList.stream()
				.map(TagVO::getId)
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());

		// 新旧标签一致、无更新
		if (tagVOList.size() == oldTagIdSet.size()
				&& newTagIdSet.containsAll(oldTagIdSet)) {
			return 0;
		}

		// 新旧标签id交集
		Set<Integer> expect = oldTagIdSet.stream()
				.filter(newTagIdSet::contains)
				.collect(Collectors.toSet());

		// 更新新标签
		int count = 0;
		for (TagVO vo : tagVOList) {
			// 可能是新标签
			if (Objects.isNull(vo.getId()) && StringUtils.hasText(vo.getName())) {
				int tagId = add(vo);
				// 新标签
				if (tagId < 0 && !oldTagIdSet.contains(-tagId) || tagId > 0) {
					addBlogTag(blogId, Math.abs(tagId));
					count++;
				}
			} else {
				if (!expect.contains(vo.getId())) {
					addBlogTag(blogId, vo.getId());
					count++;
				}
			}
		}
		// 移除旧标签
		oldTagIdSet.removeAll(expect);
		for (Integer tagId : oldTagIdSet) {
			deleteBlogTag(blogId, tagId);
			count++;
		}

		return count;
	}

	@Override
	public int deleteBlogUsed(int blogId) {
		return blogTagMapper.delete(
				Wrappers.lambdaQuery(BlogTagEntity.class)
						.eq(BlogTagEntity::getBlogId, blogId)
		);
	}

	@Override
	public int deleteBlogTag(int blogId, int tagId) {
		return blogTagMapper.delete(
				Wrappers.lambdaQuery(BlogTagEntity.class)
						.eq(BlogTagEntity::getBlogId, blogId)
						.eq(BlogTagEntity::getTagId, tagId)
		);
	}

	@Override
	public TagVO covertToTagVO(TagEntity tagEntity) {
		return new TagVO().convertFrom(tagEntity);
	}

	@Override
	public List<TagVO> covertToTagVOList(@NonNull List<TagEntity> tagEntityList) {
		return tagEntityList.stream().parallel()
				.map(this::covertToTagVO)
				.collect(Collectors.toList());
	}
}
