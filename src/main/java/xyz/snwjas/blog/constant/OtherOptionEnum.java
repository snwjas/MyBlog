package xyz.snwjas.blog.constant;

/**
 * todo
 *
 * @author Myles Yang
 */
public enum OtherOptionEnum implements OptionEnum {

	URL_FAVICON_PARSER("url_favicon_parser", String.class, ""),

	;

	private final String key;

	private final Class<?> type;

	private final String defaultValue;

	OtherOptionEnum(String key, Class<?> type, String defaultValue) {
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
