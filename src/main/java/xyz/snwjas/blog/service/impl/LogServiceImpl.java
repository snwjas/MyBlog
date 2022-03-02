package xyz.snwjas.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import xyz.snwjas.blog.mapper.LogMapper;
import xyz.snwjas.blog.model.PageResult;
import xyz.snwjas.blog.model.entity.LogEntity;
import xyz.snwjas.blog.model.params.LogSearchParam;
import xyz.snwjas.blog.model.vo.LogVO;
import xyz.snwjas.blog.service.LogService;
import xyz.snwjas.blog.utils.IPUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Log Service Impl
 *
 * @author Myles Yang
 */
@Service
public class LogServiceImpl implements LogService {

	@Resource
	private LogMapper logMapper;

	@Override
	public IPage<LogEntity> pageBy(LogSearchParam param) {
		Page<LogEntity> page = new Page<>(param.getCurrent(), param.getPageSize());
		Wrapper<LogEntity> wrapper = Wrappers.lambdaQuery(LogEntity.class)
				.in(!CollectionUtils.isEmpty(param.getTypes()), LogEntity::getType, param.getTypes())
				.orderByDesc(LogEntity::getId);
		return logMapper.selectPage(page, wrapper);
	}

	@Override
	public int add(LogVO logVO) {
		LogEntity logEntity = covertToEntity(logVO);
		logMapper.insert(logEntity);
		return logEntity.getId();
	}

	@Override
	public int deleteById(int logId) {
		return logMapper.deleteById(logId);
	}

	@Override
	public int deleteByIds(@NonNull List<Integer> ids) {
		Set<Integer> idSet = ids.stream()
				.filter(id -> Objects.nonNull(id) && id > 0)
				.collect(Collectors.toSet());
		if (idSet.isEmpty()) {
			return 0;
		}
		return logMapper.deleteBatchIds(idSet);
	}

	@Override
	public LogVO covertToVO(LogEntity logEntity) {
		LogVO logVO = new LogVO().convertFrom(logEntity);
		Integer intIpv4 = logEntity.getIpAddress();
		if (Objects.nonNull(intIpv4)) {
			logVO.setIpAddress(IPUtils.intToIpv4(intIpv4));
		}
		return logVO;
	}

	@Override
	public LogEntity covertToEntity(LogVO logVO) {
		LogEntity logEntity = logVO.convertTo(new LogEntity());
		boolean iPv4Valid = IPUtils.isIPv4Valid(logVO.getIpAddress());
		if (iPv4Valid) {
			int intIpv4 = IPUtils.ipv4ToInt(logVO.getIpAddress());
			logEntity.setIpAddress(intIpv4);
		}
		return logEntity;
	}


	@Override
	public List<LogVO> covertToListVO(@NonNull List<LogEntity> logEntityList) {
		return logEntityList.stream().parallel()
				.map(this::covertToVO)
				.collect(Collectors.toList());
	}

	@Override
	public PageResult<LogVO> covertToPageResult(IPage<LogEntity> page) {
		List<LogVO> list = covertToListVO(page.getRecords());
		return new PageResult<>(page.getTotal(), list);
	}

}
