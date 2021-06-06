package xyz.snwjas.blog.controller.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.snwjas.blog.config.LocalDateTimeSerializerConfig;
import xyz.snwjas.blog.model.R;
import xyz.snwjas.blog.model.vo.StatisticsBasicVO;
import xyz.snwjas.blog.model.vo.StatisticsReportVo;
import xyz.snwjas.blog.service.impl.StatisticsServiceImpl;
import xyz.snwjas.blog.utils.RUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Admin Controller
 *
 * @author Myles Yang
 */
@Validated
@RestController("AdminStatisticsController")
@RequestMapping("/api/admin/statistics")
@Api(value = "后台统计控制器", tags = {"后台统计接口"})
public class StatisticsController {

	@Autowired
	private StatisticsServiceImpl statisticsService;


	@GetMapping("/common")
	@ApiOperation("获取基本的统计信息")
	public R getCommonStatistics() {
		StatisticsBasicVO statisticsBasicVo = statisticsService.getCommonStatistics();
		return RUtils.success("统计信息", statisticsBasicVo);
	}

	@GetMapping("/daily/{start}/{end}")
	@ApiOperation("获取每日的统计信息")
	public R getDailyStatistics(@PathVariable("start") @DateTimeFormat(pattern =
			LocalDateTimeSerializerConfig.DEFAULT_DATE_TIME_PATTERN) LocalDateTime start,
	                            @PathVariable("end") @DateTimeFormat(pattern =
            LocalDateTimeSerializerConfig.DEFAULT_DATE_TIME_PATTERN) LocalDateTime end) {
		List<StatisticsReportVo> list = statisticsService.getDailyStatistics(start, end);
		return RUtils.success("每日的统计数据", list);
	}

}
