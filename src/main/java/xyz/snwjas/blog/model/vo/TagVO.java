package xyz.snwjas.blog.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import xyz.snwjas.blog.model.base.BeanConvert;
import xyz.snwjas.blog.model.base.ValidGroupType.Save;
import xyz.snwjas.blog.model.base.ValidGroupType.Update;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Tag VO
 *
 * @author Myles Yang
 */
@Data
@Accessors(chain = true)
@ApiModel("标签视图对象")
public class TagVO implements BeanConvert, Serializable {

	private static final long serialVersionUID = -2783711626542947755L;

	@NotNull(groups = {Update.class})
	@Min(value = 1, groups = {Update.class})
	@ApiModelProperty("标签ID")
	private Integer id;

	@NotBlank(message = "标签名不能为空", groups = {Save.class, Update.class})
	@Length(min = 1, max = 63, message = "标签名长度应介于1至63之间", groups = {Save.class, Update.class})
	@ApiModelProperty("标签名")
	private String name;

}
