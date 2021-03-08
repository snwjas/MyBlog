package xyz.snwjas.blog.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
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
		// 默认
		String blacklist = "/wordfilter/blacklist.txt";
		String whitelist = "/wordfilter/whitelist.txt";
		log.info("加载敏感词过滤器...");
		WordContext wordContext = new WordContext(blacklist, whitelist);
		WordFilter wordFilter = new WordFilter(wordContext);
		log.info("敏感词过滤器加载完毕。");
		return wordFilter;
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

	/**
	 * error page 返回json
	 */
	@Bean("error")
	public View error() {
		ModelAndView view = new ModelAndView(new MappingJackson2JsonView());
		return view.getView();
	}

}
