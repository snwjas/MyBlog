package xyz.snwjas.blog.service;

import xyz.snwjas.blog.model.vo.StatisticsBasicVO;
import xyz.snwjas.blog.model.vo.StatisticsReportVo;

import java.util.List;

/**
 * todo
 *
 * @author Myles Yang
 */
public interface StatisticsService {

	/**
	 * 获取基本的统计信息
	 */
	StatisticsBasicVO getCommonStatistics();

	/**
	 * 获取 limit 天的统计信息
	 */
	List<StatisticsReportVo> getByLimitAndOrder(int limit, boolean isDesc);

	/**
	 * 获取网站总访问量
	 */
	int getWebTotalVisit();
}
