package xyz.snwjas.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.lang.NonNull;
import xyz.snwjas.blog.model.PageResult;
import xyz.snwjas.blog.model.entity.LogEntity;
import xyz.snwjas.blog.model.params.LogSearchParam;
import xyz.snwjas.blog.model.vo.LogVO;

import java.util.List;

/**
 * Log Service
 *
 * @author Myles Yang
 */
public interface LogService {

	/**
	 * 条件分页搜索日志，默认按id倒序查询
	 */
	IPage<LogEntity> pageBy(LogSearchParam param);

	/**
	 * 添加日志
	 */
	int add(LogVO logVO);

	/**
	 * 删除日志
	 */
	int deleteById(int logId);

	/**
	 * 批量删除日志
	 */
	int deleteByIds(@NonNull List<Integer> ids);

	LogVO covertToVO(LogEntity logEntity);

	LogEntity covertToEntity(LogVO logVO);

	List<LogVO> covertToListVO(List<LogEntity> logEntityList);

	PageResult<LogVO> covertToPageResult(IPage<LogEntity> page);
}
