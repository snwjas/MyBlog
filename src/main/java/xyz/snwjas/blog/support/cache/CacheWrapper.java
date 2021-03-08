package xyz.snwjas.blog.support.cache;

import lombok.Data;

import java.util.Date;

/**
 * 缓存数据包装类
 *
 * @param <V> 数据类型
 */
@Data
class CacheWrapper<V> {

	/**
	 * 缓存数据
	 */
	private V data;

	/**
	 * 创建时间
	 */
	private Date createAt;

	/**
	 * 过期时间
	 */
	private Date expireAt;

	public CacheWrapper(V data) {
		this.data = data;
	}

	public CacheWrapper(V data, Date createAt, Date expireAt) {
		this.data = data;
		this.createAt = createAt;
		this.expireAt = expireAt;
	}


}
