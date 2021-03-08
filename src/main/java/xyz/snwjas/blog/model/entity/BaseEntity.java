package xyz.snwjas.blog.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基础实体
 *
 * @author Myles Yang
 */
@Data
@EqualsAndHashCode
public class BaseEntity implements Serializable {

	private static final long serialVersionUID = -6489406991618570904L;

	@ApiModelProperty(value = "更新时间")
	@TableField(value = "update_time")
	private LocalDateTime updateTime;

	@ApiModelProperty(value = "创建时间")
	@TableField(value = "create_time")
	private LocalDateTime createTime;
}
