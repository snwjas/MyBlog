package xyz.snwjas.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.snwjas.blog.constant.MyBlogOptionEnum;
import xyz.snwjas.blog.mapper.StatisticsMapper;
import xyz.snwjas.blog.model.entity.StatisticsEntity;
import xyz.snwjas.blog.model.enums.BlogStatus;
import xyz.snwjas.blog.model.enums.CommentStatus;
import xyz.snwjas.blog.model.vo.StatisticsBasicVO;
import xyz.snwjas.blog.model.vo.StatisticsReportVo;
import xyz.snwjas.blog.service.*;
import xyz.snwjas.blog.utils.LambdaTypeUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 统计表（统计每日的数据） 服务实现类
 * </p>
 *
 * @author Myles Yang
 */
@Service
@Slf4j
public class StatisticsServiceImpl implements StatisticsService {

	@Resource
	private StatisticsMapper statisticsMapper;

	@Autowired
	private BlogServiceImpl blogService;

	@Autowired
	private CommentService commentService;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private TagService tagService;

	@Autowired
	private LinkService linkService;

	@Autowired
	private OptionsService optionsService;

	@Override
	public StatisticsBasicVO getCommonStatistics() {

		StatisticsBasicVO statistics = new StatisticsBasicVO();

		statistics.setBlogCount(blogService.getCount(BlogStatus.PUBLISHED)) // 博客总数
				.setCommentCount(commentService.getCountByStatus(CommentStatus.PUBLISHED)) // 评论总数
				.setCategoryCount(categoryService.getCount()) // 分类总数
				.setTagCount(tagService.getCount()) // 标签总数
				.setLinkCount(linkService.getCount()) // 友链总数
				.setBlogTotalVisits(blogService.getTotalVisits()) // 博客访问总数
				.setWebTotalVisits(getWebTotalVisit()); // 网站总访问量

		// 建立日期
		LocalDateTime birthday = null;
		try {
			String date = optionsService.get(MyBlogOptionEnum.BIRTHDAY.key()).getOptionValue();
			birthday = LocalDateTime.parse(date);
		} catch (Exception e) {
			log.error("获取建站日期失败", e.getCause());
		}
		statistics.setBirthday(birthday = Objects.isNull(birthday) ? LocalDateTime.now() : birthday);
		// 时间差（建立天数）
		int days = (int) ChronoUnit.DAYS.between(birthday, LocalDateTime.now());
		statistics.setEstablishDaysCount(days);

		return statistics;
	}

	@Override
	public List<StatisticsReportVo> getByLimitAndOrder(int limit, boolean isDesc) {

		Page<StatisticsEntity> page = new Page<>(1, limit, false);

		LambdaQueryWrapper<StatisticsEntity> wrapper = new LambdaQueryWrapper<>();
		if (isDesc) {
			wrapper.orderByDesc(StatisticsEntity::getId);
		}

		List<StatisticsReportVo> result = new ArrayList<>();
		List<StatisticsEntity> records = statisticsMapper.selectPage(page, wrapper).getRecords();
		records.forEach(entity -> result.add(new StatisticsReportVo().convertFrom(entity)));

		if (isDesc) {
			Collections.reverse(result);
		}

		return result;
	}

	@Override
	public int getWebTotalVisit() {
		return statisticsMapper.sum(
				LambdaTypeUtils.getColumnName(StatisticsEntity::getWebVisitCount),
				Wrappers.emptyWrapper()
		);
	}

	// 获取近 n 天的统计数据
	public List<StatisticsReportVo> getDailyStatistics(int numOfDays) {
		return getByLimitAndOrder(numOfDays, true);
	}
}
