package xyz.snwjas.blog.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import xyz.snwjas.blog.model.base.BeanConvert;
import xyz.snwjas.blog.model.base.ValidGroupType;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Link VO
 *
 * @author Myles Yang
 */
@Data
@Accessors(chain = true)
@ApiModel("友链视图对象")
public class LinkVO implements BeanConvert, Serializable {

	private static final long serialVersionUID = 4860407888308055369L;

	@NotNull(groups = {ValidGroupType.Update.class})
	@Min(value = 1, groups = {ValidGroupType.Update.class})
	@ApiModelProperty("友链ID")
	private Integer id;

	@NotBlank(groups = {ValidGroupType.Save.class, ValidGroupType.Update.class}, message = "友链名称不能为空")
	@Length(max = 127, groups = {ValidGroupType.Save.class, ValidGroupType.Update.class})
	@ApiModelProperty("友链名")
	private String name;

	@NotBlank(groups = {ValidGroupType.Save.class, ValidGroupType.Update.class}, message = "友链地址不能为空")
	@Length(max = 255, groups = {ValidGroupType.Save.class, ValidGroupType.Update.class})
	@ApiModelProperty("友链地址")
	private String url;

	@Length(max = 1023)
	@ApiModelProperty("友链Logo地址")
	private String logo;

	@ApiModelProperty("友链排行（值越大越靠前）")
	private Integer topRank;

	@Length(max = 512)
	@ApiModelProperty("友链描述")
	private String description;

}
