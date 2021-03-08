package xyz.snwjas.blog.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * Log Type enum
 *
 * @author Myles Yang
 */
public enum LogType implements ValueEnum<Integer> {

	COMMON(0),

	LOGGED_IN(1),

	LOGGED_OUT(2),

	LOGIN_FAILED(3),

	PASSWORD_UPDATED(4),

	PROFILE_UPDATED(5),

	BLOG_PUBLISHED(6),

	BLOG_EDITED(7),

	BLOG_DELETED(8),

	OPTION_UPDATE(9),
	;

	@EnumValue
	private final Integer value;

	LogType(Integer value) {
		this.value = value;
	}

	@Override
	public Integer getValue() {
		return value;
	}
}
