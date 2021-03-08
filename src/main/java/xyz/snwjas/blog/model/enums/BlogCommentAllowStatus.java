package xyz.snwjas.blog.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * 博客评论允许状态枚举常量
 *
 * @author Myles Yang
 */
public enum BlogCommentAllowStatus implements ValueEnum<Integer> {
	/**
	 * 不允许
	 */
	UNALLOWED(0),

	/**
	 * 允许,但需审核
	 */
	ALLOWED_AUDITING(1),

	/**
	 * 允许,自动审核
	 */
	ALLOWED_PASSAUTO(2);

	@EnumValue
	private final int value;

	BlogCommentAllowStatus(int value) {
		this.value = value;
	}

	@Override
	public Integer getValue() {
		return value;
	}
}
