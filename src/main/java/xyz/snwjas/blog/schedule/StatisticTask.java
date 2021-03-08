package xyz.snwjas.blog.schedule;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import xyz.snwjas.blog.constant.CacheKeyPrefix;
import xyz.snwjas.blog.mapper.BlogMapper;
import xyz.snwjas.blog.mapper.CommentMapper;
import xyz.snwjas.blog.mapper.StatisticsMapper;
import xyz.snwjas.blog.model.entity.BlogEntity;
import xyz.snwjas.blog.model.entity.CommentEntity;
import xyz.snwjas.blog.model.entity.StatisticsEntity;
import xyz.snwjas.blog.support.cache.MemoryCacheStore;
import xyz.snwjas.blog.utils.DateUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据统计定时任务
 *
 * @author Myles Yang
 */
@Component
@Slf4j
public class StatisticTask {

	@Autowired
	private MemoryCacheStore cache;

	@Resource
	private StatisticsMapper statisticsMapper;

	@Resource
	private CommentMapper commentMapper;

	@Resource
	private BlogMapper blogMapper;

/*	@PostConstruct
	public void run() {
		autoStat();
	}*/

	// 每天零点统计信息
	@Async("executor")
	@Scheduled(cron = "1 0 0 */1 * ?")
	public void autoStat() {
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

	// 统计每个博客的访问量
	public void statisticsEveryBlogVisit(Set<String> blogVisitKeys) {
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
