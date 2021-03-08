package xyz.snwjas.blog.controller.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.snwjas.blog.model.R;
import xyz.snwjas.blog.model.vo.StatisticsBasicVO;
import xyz.snwjas.blog.model.vo.StatisticsReportVo;
import xyz.snwjas.blog.service.impl.StatisticsServiceImpl;
import xyz.snwjas.blog.utils.RUtils;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * Admin Controller
 *
 * @author Myles Yang
 */
@Validated
@RestController("AdminStatisticsController")
@RequestMapping("/api/admin/statistic")
@Api(value = "后台统计控制器", tags = {"后台统计接口"})
public class StatisticsController {

	@Autowired
	private StatisticsServiceImpl statisticsService;


	@GetMapping("")
	@ApiOperation("获取基本的统计信息")
	public R getCommonStatistics() {
		StatisticsBasicVO statisticsBasicVo = statisticsService.getCommonStatistics();
		return RUtils.success("统计信息", statisticsBasicVo);
	}

	@GetMapping("/{days}")
	@ApiOperation("获取近些天的统计信息")
	public R getDailyStatistics(@PathVariable("days") @Min(7) @Max(90) Integer days) {
		List<StatisticsReportVo> list = statisticsService.getDailyStatistics(days);
		return RUtils.success("近" + days + "天的统计数据", list);
	}

}
