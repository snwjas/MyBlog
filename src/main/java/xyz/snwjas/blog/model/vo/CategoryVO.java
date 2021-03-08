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
 * Category VO
 *
 * @author Myles Yang
 */
@Data
@Accessors(chain = true)
@ApiModel("分类视图对象")
public class CategoryVO implements BeanConvert, Serializable {

	private static final long serialVersionUID = 7673727208979429235L;

	@NotNull(groups = {Update.class})
	@Min(value = 1, groups = {Update.class})
	@ApiModelProperty("分类ID")
	private Integer id;

	@NotBlank(message = "分类名不能为空", groups = {Save.class, Update.class})
	@Length(min = 1, max = 63, message = "分类名长度应介于1至63之间", groups = {Save.class, Update.class})
	@ApiModelProperty("分类名")
	private String name;

	@Length(max = 127, message = "分类描述长度应小于127", groups = {Save.class, Update.class})
	@ApiModelProperty("分类描述")
	private String description;

	@ApiModelProperty("该分类下的博客数量")
	private Integer blogCount;
}
