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


	/**
	 * 获得optionValue的原类型值
	 */
	@SuppressWarnings("unchecked")
	static <T> T getTrueOptionValue(String value, Class<T> type) {
		if (type.isAssignableFrom(String.class)) {
			return (T) value;
		}

		if (type.isAssignableFrom(Integer.class)) {
			return (T) Integer.valueOf(value);
		}

		if (type.isAssignableFrom(Long.class)) {
			return (T) Long.valueOf(value);
		}

		if (type.isAssignableFrom(Boolean.class)) {
			return (T) Boolean.valueOf(value);
		}

		if (type.isAssignableFrom(Short.class)) {
			return (T) Short.valueOf(value);
		}

		if (type.isAssignableFrom(Byte.class)) {
			return (T) Byte.valueOf(value);
		}

		if (type.isAssignableFrom(Double.class)) {
			return (T) Double.valueOf(value);
		}

		if (type.isAssignableFrom(Float.class)) {
			return (T) Float.valueOf(value);
		}

		return (T) value;
	}

}
