package xyz.snwjas.blog.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.snwjas.blog.constant.CacheKeyPrefix;
import xyz.snwjas.blog.constant.MyBlogOptionEnum;
import xyz.snwjas.blog.mapper.BlogMapper;
import xyz.snwjas.blog.mapper.CommentMapper;
import xyz.snwjas.blog.mapper.StatisticsMapper;
import xyz.snwjas.blog.model.entity.BlogEntity;
import xyz.snwjas.blog.model.entity.CommentEntity;
import xyz.snwjas.blog.model.entity.StatisticsEntity;
import xyz.snwjas.blog.model.enums.BlogStatus;
import xyz.snwjas.blog.model.enums.CommentStatus;
import xyz.snwjas.blog.model.vo.StatisticsBasicVO;
import xyz.snwjas.blog.model.vo.StatisticsReportVo;
import xyz.snwjas.blog.service.*;
import xyz.snwjas.blog.support.cache.MemoryCacheStore;
import xyz.snwjas.blog.utils.DateUtils;
import xyz.snwjas.blog.utils.LambdaTypeUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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

	@Resource
	private CommentMapper commentMapper;

	@Resource
	private BlogMapper blogMapper;

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

	@Autowired
	private MemoryCacheStore cache;


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
	public List<StatisticsReportVo> getDailyStatistics(LocalDateTime start, LocalDateTime end) {

		List<StatisticsEntity> entityList = statisticsMapper.selectList(
				Wrappers.lambdaQuery(StatisticsEntity.class)
						.ge(StatisticsEntity::getDate, start)
						.le(StatisticsEntity::getDate, end)
		);

		return entityList.stream().parallel()
				.map(e -> (StatisticsReportVo) new StatisticsReportVo().convertFrom(e))
				.collect(Collectors.toList());
	}

	@Override
	public int getWebTotalVisit() {
		return statisticsMapper.sum(
				LambdaTypeUtils.getColumnName(StatisticsEntity::getWebVisitCount),
				Wrappers.emptyWrapper()
		);
	}

	@Override
	public void statisticsDaily() {
		log.info("开始统计信息...");

		LocalDateTime yesterday = DateUtils.getZeroDateTime(1);

		// 判断昨日是否已统计
		StatisticsEntity yesterdayStat = statisticsMapper.selectOne(
				Wrappers.lambdaQuery(StatisticsEntity.class)
						.select(StatisticsEntity::getId)
						.eq(StatisticsEntity::getDate, yesterday)
		);
		if (Objects.nonNull(yesterdayStat)) {
			log.info("结束统计信息，已统计过了...");
			return;
		}

		// 评论新增量
		Integer commentPublishCount = commentMapper.selectCount(
				Wrappers.lambdaQuery(CommentEntity.class)
						.gt(CommentEntity::getCreateTime, yesterday)
						.lt(CommentEntity::getCreateTime, DateUtils.getZeroDateTime(0))
		);

		// CacheKeyPrefix.BLOG_VISIT_COUNT + blogContent.getId() + ":" + ipAddress;
		Set<String> blogVisitKeys = cache.keys(CacheKeyPrefix.BLOG_VISIT_COUNT + "*");
		Set<String> webVisitKeys = cache.keys(CacheKeyPrefix.WEB_VISIT_COUNT + "*");

		int blogVisitCount = blogVisitKeys.size();
		int webVisitCount = webVisitKeys.size();

		// 统计每个博客的访问量
		statisticsEveryBlogVisit(blogVisitKeys);

		// 清除缓存
		cache.delete(blogVisitKeys);
		cache.delete(webVisitKeys);

		// 保存数据库
		StatisticsEntity statisticsEntity = new StatisticsEntity()
				.setCommentCount(Objects.isNull(commentPublishCount) ? 0 : commentPublishCount)
				.setWebVisitCount(webVisitCount)
				.setBlogVisitCount(blogVisitCount)
				.setDate(yesterday);
		statisticsMapper.insert(statisticsEntity);

		log.info("结束统计信息...");
	}

	/**
	 * 统计每个博客的访问量
	 */
	private void statisticsEveryBlogVisit(Set<String> blogVisitKeys) {
		// 存放 博客id 和 对应访问量
		ConcurrentHashMap<Integer, Integer> countMap = new ConcurrentHashMap<>();
		blogVisitKeys.stream()
				.parallel()
				.map(k -> k.split(CacheKeyPrefix.SEPARATOR)[1])
				.map(Integer::parseInt)
				.forEach(blogId -> {
					Integer count = countMap.get(blogId);
					if (Objects.isNull(count)) {
						countMap.put(blogId, 1);
					} else {
						countMap.put(blogId, count + 1);
					}
				});
		// 保存数据库
		for (Map.Entry<Integer, Integer> es : countMap.entrySet()) {
			BlogEntity blog = blogMapper.selectOne(
					Wrappers.lambdaQuery(BlogEntity.class)
							.select(BlogEntity::getVisits)
							.eq(BlogEntity::getId, es.getKey())
			);

			if (Objects.isNull(blog)) {
				continue;
			}

			int visits = Objects.isNull(blog.getVisits())
					? es.getValue()
					: blog.getVisits() + es.getValue();

			blogMapper.update(null,
					Wrappers.lambdaUpdate(BlogEntity.class)
							.eq(BlogEntity::getId, es.getKey())
							.set(BlogEntity::getVisits, visits)
			);
		}

	}

}
