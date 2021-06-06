package xyz.snwjas.blog.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import xyz.snwjas.blog.service.StatisticsService;

/**
 * 数据统计定时任务
 *
 * @author Myles Yang
 */
@Component
@Slf4j
public class StatisticTask {

	@Autowired
	private StatisticsService statisticsService;

/*	@PostConstruct
	public void run() {
		autoStat();
	}*/

	// 每天零点统计信息
	@Async("executor")
	@Scheduled(cron = "1 0 0 */1 * ?")
	public void autoStat() {
		statisticsService.statisticsDaily();
	}

}
