package xyz.snwjas.blog.constant;

/**
 * Redis key 前缀 常量
 *
 * @author Myles Yang
 */
public interface CacheKeyPrefix {

	/**
	 * 分隔符
	 */
	String SEPARATOR = ":";

	/**
	 * 认证失败次数前缀
	 */
	String LOGIN_FAILED_COUNT = "loginFailedCount:";

	/**
	 * 访问限制次数（接口限流）
	 */
	String ACCESS_LIMIT_PREFIX = "accessLimit:";

	/**
	 * 网站访问量
	 */
	String WEB_VISIT_COUNT = "webVisitCount:";

	/**
	 * 博客文章访问量
	 */
	String BLOG_VISIT_COUNT = "blogVisitCount:";

	/**
	 * 博客点赞
	 */
	String BLOG_LIKE = "blogLike:";

}
