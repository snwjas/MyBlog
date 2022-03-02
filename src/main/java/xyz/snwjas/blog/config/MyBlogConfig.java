package xyz.snwjas.blog.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import xyz.snwjas.blog.support.cache.MemoryCacheStore;
import xyz.snwjas.blog.support.wordfilter.WordContext;
import xyz.snwjas.blog.support.wordfilter.WordFilter;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 博客配置
 *
 * @author Myles Yang
 */
@Configuration
@Slf4j
public class MyBlogConfig {

	/**
	 * 自定义缓存
	 */
	@Bean
	public MemoryCacheStore memoryCacheStore() {
		return new MemoryCacheStore();
	}

	/**
	 * 敏感词过滤器
	 */
	@Bean
	public WordFilter wordFilter() {
		return new WordFilter(new WordContext());
	}

	/**
	 * 线程池
	 */
	@Bean("executor")
	public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(Runtime.getRuntime().availableProcessors());
		executor.setMaxPoolSize(128);
		executor.setQueueCapacity(1024);
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
		executor.setWaitForTasksToCompleteOnShutdown(true);
		executor.setAwaitTerminationSeconds(3);
		return executor;
	}

}
