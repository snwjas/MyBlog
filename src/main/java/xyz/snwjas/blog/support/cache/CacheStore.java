package xyz.snwjas.blog.support.cache;

/**
 * 自定义缓存接口
 *
 * @author Myles Yang
 */
public interface CacheStore<K, V> {

	/**
	 * 设置一个会过期的缓存
	 *
	 * @param key     键
	 * @param value   值
	 * @param seconds 秒
	 */
	void set(K key, V value, int seconds);

	/**
	 * 设置一个不会过期的缓存
	 *
	 * @param key   键
	 * @param value 值
	 */
	void set(K key, V value);


	/**
	 * 获取一个缓存
	 *
	 * @param key 键
	 * @return 缓存值，如果键不存在返回 null
	 */
	V get(K key);

	/**
	 * 删除一个缓存
	 *
	 * @param key 键
	 * @return true : 删除成功 ; false : 键不存在
	 */
	boolean delete(K key);

	/**
	 * 是否存在缓存
	 *
	 * @param key 键
	 * @return true : 存在 ; false : 不存在
	 */
	boolean containsKey(K key);

	/**
	 * 清空缓存
	 */
	void clear();
}
