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
 * 友链
 * </p>
 *
 * @author Myles Yang
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("link")
@ApiModel(value = "LinksEntity对象", description = "友链")
public class LinkEntity extends BaseEntity {

	private static final long serialVersionUID = 4547836104191474484L;

	@ApiModelProperty(value = "友链id")
	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;

	@ApiModelProperty(value = "友链名称")
	@TableField("name")
	private String name;

	@ApiModelProperty(value = "友链链接")
	@TableField("url")
	private String url;

	@ApiModelProperty(value = "友链logo")
	@TableField("logo")
	private String logo;

	@ApiModelProperty(value = "友链排行")
	@TableField("top_rank")
	private Integer topRank;

	@ApiModelProperty(value = "友链描述")
	@TableField("description")
	private String description;

}
