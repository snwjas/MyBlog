package xyz.snwjas.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import xyz.snwjas.blog.mapper.SpecaillistMapper;
import xyz.snwjas.blog.model.PageResult;
import xyz.snwjas.blog.model.entity.SpeciallistEntity;
import xyz.snwjas.blog.model.enums.SpecialListType;
import xyz.snwjas.blog.model.params.SpeciallistSearchParam;
import xyz.snwjas.blog.model.vo.SpeciallistVO;
import xyz.snwjas.blog.service.SpeciallistService;
import xyz.snwjas.blog.support.wordfilter.WordFilter;
import xyz.snwjas.blog.support.wordfilter.WordType;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * Special List Service Impl
 *
 * @author Myles Yang
 */
@Service
@Slf4j
public class SpeciallistServiceImpl extends ServiceImpl<SpecaillistMapper, SpeciallistEntity> implements SpeciallistService {

	private final Lock lock = new ReentrantLock();

	@Resource
	private SpecaillistMapper mapper;

	@Autowired
	private WordFilter wordFilter;


	@Override
	public List<SpeciallistVO> listEnumType() {
		return Arrays.stream(SpecialListType.values())
				.map(e -> new SpeciallistVO(e, String.valueOf(e.getValue())))
				.collect(Collectors.toList());
	}

	@Override
	public List<SpeciallistVO> listAll(SpecialListType... types) {
		List<SpeciallistEntity> entityList = mapper.selectList(
				Wrappers.lambdaQuery(SpeciallistEntity.class)
						.select(SpeciallistEntity::getType, SpeciallistEntity::getContent)
						.in(SpeciallistEntity::getType, types)
		);
		return covertToListVO(entityList);
	}

	@Override
	public IPage<SpeciallistEntity> pageBy(SpeciallistSearchParam param) {
		Page<SpeciallistEntity> page = new Page<>(param.getCurrent(), param.getPageSize());
		Wrapper<SpeciallistEntity> wrapper = Wrappers.lambdaQuery(SpeciallistEntity.class)
				.in(!CollectionUtils.isEmpty(param.getTypes()), SpeciallistEntity::getType, param.getTypes())
				.like(StringUtils.isNotEmpty(param.getContent()), SpeciallistEntity::getContent, param.getContent());
		return mapper.selectPage(page, wrapper);
	}

	@Override
	public int add(SpeciallistVO vo) {
		SpeciallistEntity entity = vo.convertTo(new SpeciallistEntity());
		mapper.insert(entity);
		return entity.getId();
	}

	@Override
	public boolean addBatch(SpecialListType type, List<String> contents) {
		List<SpeciallistEntity> entityList = contents.stream().parallel()
				.filter(StringUtils::isNotEmpty)
				.distinct()
				.map(c -> new SpeciallistEntity(type, c))
				.collect(Collectors.toList());
		return saveBatch(entityList, 1000);
	}

	@Override
	public int deleteById(int id) {
		return mapper.deleteById(id);
	}

	@Override
	public int deleteByIds(List<Integer> ids) {
		Set<Integer> idSet = ids.stream()
				.filter(id -> Objects.nonNull(id) && id > 0)
				.collect(Collectors.toSet());
		if (idSet.isEmpty()) {
			return 0;
		}
		return mapper.deleteBatchIds(idSet);
	}

	@Override
	public boolean refreshContext(SpecialListType type) {
		lock.lock();
		try {
			switch (type) {
				case WORD_BLACK_LIST:
					Set<String> blackList = listAll(SpecialListType.WORD_BLACK_LIST).stream().parallel()
							.map(SpeciallistVO::getContent)
							.filter(StringUtils::isNotEmpty)
							.collect(Collectors.toSet());
					this.wordFilter.getContext().removeWord(WordType.BLACK);
					this.wordFilter.getContext().addWord(blackList, WordType.BLACK);
					log.info("重载敏感词白名单 {} 条", blackList.size());
					break;
				case WORD_WHITE_LIST:
					Set<String> whiteList = listAll(SpecialListType.WORD_WHITE_LIST).stream().parallel()
							.map(SpeciallistVO::getContent)
							.filter(StringUtils::isNotEmpty)
							.collect(Collectors.toSet());
					this.wordFilter.getContext().removeWord(WordType.WHITE);
					this.wordFilter.getContext().addWord(whiteList, WordType.WHITE);
					log.info("重载敏感词黑名单 {} 条", whiteList.size());
					break;
			}
		} finally {
			lock.unlock();
		}
		return true;
	}

	@Override
	public SpeciallistVO covertToVO(SpeciallistEntity entity) {
		return new SpeciallistVO().convertFrom(entity);
	}

	@Override
	public List<SpeciallistVO> covertToListVO(List<SpeciallistEntity> entityList) {
		return entityList.stream().parallel()
				.map(this::covertToVO)
				.collect(Collectors.toList());
	}

	@Override
	public PageResult<SpeciallistVO> covertToPageResult(IPage<SpeciallistEntity> page) {
		List<SpeciallistVO> list = covertToListVO(page.getRecords());
		return new PageResult<>(page.getTotal(), list);
	}
}
