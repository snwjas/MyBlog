package xyz.snwjas.blog.support.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 基于内存的简单键值缓存实现
 *
 * @author Myles Yang
 */
@Slf4j
public class MemoryCacheStore implements CacheStore<String, Object> {

	/**
	 * 缓存容器
	 */
	private final ConcurrentHashMap<String, CacheWrapper<Object>> cacheContainer;

	/**
	 * 计时器，清除过期缓存
	 */
	private final Timer timer;


	/**
	 * @param initialCacheCapacity      缓存初始容量
	 * @param cacheClearingPeriodMillis 自动清除过期缓存的周期
	 */
	public MemoryCacheStore(int initialCacheCapacity, int cacheClearingPeriodMillis) {
		this.cacheContainer = new ConcurrentHashMap<>(initialCacheCapacity);
		this.timer = new Timer();
		timer.scheduleAtFixedRate(new CacheExpiryCleaner(), 0, cacheClearingPeriodMillis);
	}

	public MemoryCacheStore() {
		this(256, 60 * 1000);
	}

	/**
	 * 根据正则匹配缓存键
	 *
	 * @param pattern 正则表达式
	 * @return 匹配结果
	 */
	public Set<String> keys(String pattern) {
		Assert.hasText(pattern, "表达式不能为空");

		if ("*".equals(pattern)) {
			return cacheContainer.keySet();
		}

		Pattern patn = Pattern.compile(pattern);
		try {
			return cacheContainer.keySet().stream()
					.parallel()
					.filter(key -> patn.matcher(key).find())
					.collect(Collectors.toSet());
		} catch (Exception e) {
			log.error("表达式错误", e.getCause());
		}
		return Collections.emptySet();
	}

	@Override
	public void set(String key, Object value, int seconds) {
		Assert.hasText(key, "键不能为空");
		Assert.isTrue(seconds > 0, "过期时间必须大于0");

		CacheWrapper<Object> cacheWrapper = cacheContainer.get(key);
		// 如果缓存过期，则设置新的，否则只设置值
		if (Objects.isNull(cacheWrapper)) {
			Date createAt = new Date();
			Date expireAt = new Date(createAt.getTime() + seconds * 1000L);
			cacheContainer.put(key, new CacheWrapper<>(value, createAt, expireAt));
		} else {
			cacheWrapper.setData(value);
			cacheContainer.put(key, cacheWrapper);
		}
	}

	@Override
	public void set(String key, Object value) {
		Assert.hasText(key, "键不能为空");

		cacheContainer.put(key, new CacheWrapper<>(value));
	}

	/**
	 * 如果没有该缓存，则设置一个不会过期的缓存
	 *
	 * @param key   键
	 * @param value 值
	 * @return false: 缓存已存在；true：设置成功
	 */
	public boolean setIfAbsent(String key, Object value) {
		Assert.hasText(key, "键不能为空");

		if (cacheContainer.containsKey(key)) {
			return false;
		}
		set(key, value);

		return true;
	}


	@Override
	public Object get(String key) {
		Assert.hasText(key, "键不能为空");

		CacheWrapper<Object> cacheWrapper = cacheContainer.get(key);

		// 没有该键
		if (Objects.isNull(cacheWrapper)) {
			return null;
		}

		// 缓存已过期
		Date expireAt = cacheWrapper.getExpireAt();
		if (expireAt != null && expireAt.before(new Date())) {
			log.debug("缓存: [{}] 已过期", key);
			delete(key);
			return null;
		}

		return cacheWrapper.getData();
	}

	@Override
	public boolean delete(String key) {
		Assert.hasText(key, "键不能为空");

		return cacheContainer.remove(key) != null;
	}

	@Override
	public boolean containsKey(String key) {
		Assert.hasText(key, "键不能为空");

		return cacheContainer.containsKey(key);
	}

	/**
	 * 批量删除缓存
	 */
	public int delete(Set<String> keys) {
		AtomicInteger count = new AtomicInteger();
		if (CollectionUtils.isEmpty(keys)) {
			return count.get();
		}
		keys.stream().parallel()
				.filter(Objects::nonNull)
				.forEach(key -> {
					if (cacheContainer.remove(key) != null) {
						count.incrementAndGet();
					}
				});
		return count.get();
	}

	@Override
	public void clear() {
		cacheContainer.clear();
	}

	@PreDestroy
	public void destroyCache() {
		log.debug("缓存已销毁");
		timer.cancel();
		clear();
	}

	/**
	 * 缓存清理器
	 */
	private class CacheExpiryCleaner extends TimerTask {
		@Override
		public void run() {
			cacheContainer.keySet().stream()
					.parallel()
					.forEach(MemoryCacheStore.this::get);
		}
	}
}
