package xyz.snwjas.blog.constant;

import java.time.LocalDateTime;

/**
 * MyBlog Option Enum
 *
 * @author Myles Yang
 */
public enum MyBlogOptionEnum implements OptionEnum {

	/**
	 * 博客主页url
	 */
	URL("url", String.class, "http://"),

	/**
	 * 博客名
	 */
	NAME("name", String.class, "MyBlog"),

	/**
	 * 博客描述
	 */
	DESCRIPTION("description", String.class, "MyBlog"),

	/**
	 * 博客logo
	 */
	LOGO("logo", String.class, ""),

	/**
	 * 博客favicon
	 */
	FAVICON("favicon", String.class, ""),

	/**
	 * 博客页脚信息
	 */
	FOOTER("footer", String.class, "Copyright © 2020 MyBlog"),

	/**
	 * 博客建立日期
	 */
	BIRTHDAY("birthday", String.class, LocalDateTime.now().toString()),


	;

	private final String key;

	private final Class<?> type;

	private final String defaultValue;

	MyBlogOptionEnum(String key, Class<?> type, String defaultValue) {
		this.key = key;
		this.type = type;
		this.defaultValue = defaultValue;
	}

	@Override
	public String key() {
		return key;
	}

	@Override
	public Class<?> type() {
		return type;
	}

	@Override
	public String defaultValue() {
		return defaultValue;
	}
}
