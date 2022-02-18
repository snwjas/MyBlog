package xyz.snwjas.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import xyz.snwjas.blog.mapper.LinkMapper;
import xyz.snwjas.blog.model.PageResult;
import xyz.snwjas.blog.model.entity.LinkEntity;
import xyz.snwjas.blog.model.params.LinkSearchParam;
import xyz.snwjas.blog.model.vo.LinkVO;
import xyz.snwjas.blog.service.LinkService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Link Service Impl
 *
 * @author Myles Yang
 */
@Service
public class LinkServiceImpl implements LinkService {

	@Resource
	private LinkMapper linkMapper;

	@Override
	public IPage<LinkEntity> pageBy(LinkSearchParam param) {
		Page<LinkEntity> page = new Page<>(param.getCurrent(), param.getPageSize());
		Wrapper<LinkEntity> wrapper = getSearchWrapper(param);
		return linkMapper.selectPage(page, wrapper);
	}

	@Override
	public int add(LinkVO vo) {
		LinkEntity linkEntity = vo.convertTo(new LinkEntity());
		linkMapper.insert(linkEntity);
		return linkEntity.getId();
	}

	@Override
	public int deleteById(int linkId) {
		return linkMapper.deleteById(linkId);
	}

	@Override
	public int update(LinkVO vo) {
		return linkMapper.update(null, getUpdateWrapper(vo));
	}

	@Override
	public int getCount() {
		return linkMapper.selectCount(null);
	}

	@Override
	public LinkVO getById(int linkId) {
		LinkEntity linkEntity = linkMapper.selectById(linkId);
		return covertToVO(linkEntity);
	}

	@Override
	public int updateLogoParser(String parser) {
		return linkMapper.updateLogoByParser(parser);
	}

	@Override
	public LinkVO covertToVO(LinkEntity linkEntity) {
		return new LinkVO().convertFrom(linkEntity);
	}

	@Override
	public List<LinkVO> covertToListVO(@NonNull List<LinkEntity> linkEntityList) {
		return linkEntityList.stream().parallel()
				.map(this::covertToVO)
				.collect(Collectors.toList());
	}

	@Override
	public PageResult<LinkVO> covertToPageResult(IPage<LinkEntity> page) {
		List<LinkVO> linkVOList = covertToListVO(page.getRecords());
		return new PageResult<>(page.getTotal(), linkVOList);
	}


	private Wrapper<LinkEntity> getSearchWrapper(LinkSearchParam param) {
		String name = param.getName();
		String url = param.getUrl();
		String keyword = param.getKeyword();

		return Wrappers.lambdaQuery(LinkEntity.class)
				.like(!StringUtils.hasText(keyword) && StringUtils.hasText(name), LinkEntity::getName, name)
				.like(!StringUtils.hasText(keyword) && StringUtils.hasText(url), LinkEntity::getUrl, url)
				.and(StringUtils.hasText(keyword), wrap -> wrap
						.like(LinkEntity::getName, keyword)
						.or()
						.like(LinkEntity::getUrl, keyword))
				.orderByDesc(LinkEntity::getTopRank)
				.orderByDesc(LinkEntity::getId)
				;
	}

	private Wrapper<LinkEntity> getUpdateWrapper(LinkVO vo) {
		String name = vo.getName();
		String url = vo.getUrl();
		String logo = vo.getLogo();
		Integer rank = vo.getTopRank();
		String description = vo.getDescription();

		return Wrappers.lambdaUpdate(LinkEntity.class)
				.eq(LinkEntity::getId, vo.getId())
				.set(StringUtils.hasText(name), LinkEntity::getName, name)
				.set(StringUtils.hasText(url), LinkEntity::getUrl, url)
				.set(StringUtils.hasText(logo), LinkEntity::getLogo, logo)
				.set(Objects.nonNull(rank) && rank >= 0, LinkEntity::getTopRank, rank)
				.set(StringUtils.hasText(description), LinkEntity::getDescription, description)
				;
	}

}
