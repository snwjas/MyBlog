package xyz.snwjas.blog.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * Special List Type enum
 *
 * @author Myles Yang
 */
public enum SpecialListType implements ValueEnum<Integer> {

	WORD_BLACK_LIST(1),

	WORD_WHITE_LIST(2),

	// IP_BLACK_LIST(1),

	// IP_WHITE_LIST(2),

	;


	@EnumValue
	private final Integer value;

	SpecialListType(Integer value) {
		this.value = value;
	}

	@Override
	public Integer getValue() {
		return value;
	}
}
