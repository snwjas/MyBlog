package xyz.snwjas.blog.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import xyz.snwjas.blog.model.enums.LogType;

/**
 * <p>
 * 日志
 * </p>
 *
 * @author Myles Yang
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("log")
@ApiModel(value = "LogEntity对象", description = "日志")
public class LogEntity extends BaseEntity {

	private static final long serialVersionUID = -217049419018602936L;

	@ApiModelProperty(value = "日志id")
	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;

	@ApiModelProperty(value = "操作内容")
	@TableField("content")
	private String content;

	@ApiModelProperty(value = "操作类型")
	@TableField("type")
	private LogType type;

	@ApiModelProperty(value = "操作人的ipv4地址，整型")
	@TableField("ip_address")
	private Integer ipAddress;

}
