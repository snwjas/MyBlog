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

/**
 * <p>
 * 系统（博客）设置
 * </p>
 *
 * @author Myles Yang
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("options")
@ApiModel(value = "OptionEntity对象", description = "系统（博客）设置")
public class OptionsEntity extends BaseEntity {

	private static final long serialVersionUID = -7423338560153552839L;

	@ApiModelProperty(value = "系统设置id")
	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;

	@ApiModelProperty(value = "键")
	@TableField("option_key")
	private String optionKey;

	@ApiModelProperty(value = "值")
	@TableField("option_value")
	private String optionValue;

}
