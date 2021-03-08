package xyz.snwjas.blog.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.core.enums.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 博客状态枚举常量
 *
 * @author Myles Yang
 */
public enum BlogStatus implements ValueEnum<Integer> {
	/**
	 * 已发表
	 */
	PUBLISHED(0),
	/**
	 * 草稿
	 */
	DRAFT(1),
	/**
	 * 回收站
	 */
	RECYCLE(2);

	@EnumValue
	private final int value;

	BlogStatus(int value) {
		this.value = value;
	}

	@Override
	public Integer getValue() {
		return value;
	}
}
