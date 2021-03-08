package xyz.snwjas.blog.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import xyz.snwjas.blog.model.base.BeanConvert;
import xyz.snwjas.blog.model.enums.LogType;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Log VO
 *
 * @author Myles Yang
 */
@Data
@Accessors(chain = true)
@ApiModel("日志视图对象")
public class LogVO implements BeanConvert, Serializable {

	private static final long serialVersionUID = -4592668651066079493L;

	@ApiModelProperty("日志ID")
	private Integer id;

	@ApiModelProperty("操作内容")
	private String content;

	@ApiModelProperty("操作类型")
	private LogType type;

	@ApiModelProperty("操作人ipv4地址")
	private String ipAddress;

	@ApiModelProperty("操作时间")
	private LocalDateTime createTime;
}
