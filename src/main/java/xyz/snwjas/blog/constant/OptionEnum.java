package xyz.snwjas.blog.constant;

/**
 * 博客设置枚举接口
 *
 * @author Myles Yang
 */
public interface OptionEnum {

	/**
	 * 设置项的唯一标识
	 */
	String key();

	/**
	 * 设置项的类型，用于解析时进行类型转换
	 */
	Class<?> type();

	/**
	 * 设置项默认值
	 */
	String defaultValue();

}
