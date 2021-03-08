package xyz.snwjas.blog.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import xyz.snwjas.blog.model.base.BeanConvert;

import java.io.Serializable;

/**
 * Option VO
 *
 * @author Myles Yang
 */
@Data
@Accessors(chain = true)
@ApiModel("设置选项视图对象")
public class OptionVO implements BeanConvert, Serializable {

	private static final long serialVersionUID = -2070361214962849320L;

	@ApiModelProperty("选项ID")
	private Integer id;

	@ApiModelProperty("选项键")
	private String optionKey;

	@ApiModelProperty("选项值")
	private String optionValue;

}
