package xyz.snwjas.blog.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * 评论状态枚举常量
 *
 * @author Myles Yang
 */
public enum CommentStatus implements ValueEnum<Integer> {
	/**
	 * 审核中
	 */
	AUDITING(0),

	/**
	 * 已发布
	 */
	PUBLISHED(1),

	/**
	 * 回收站
	 */
	RECYCLE(2);

	@EnumValue
	private final Integer value;

	CommentStatus(Integer value) {
		this.value = value;
	}

	@Override
	public Integer getValue() {
		return value;
	}
}
