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
 * 分类
 * </p>
 *
 * @author Myles Yang
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("category")
@ApiModel(value = "CategoryEntity对象", description = "分类")
public class CategoryEntity extends BaseEntity {

	private static final long serialVersionUID = -524653268318328366L;

	@ApiModelProperty(value = "分类id")
	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;

	@ApiModelProperty(value = "分类名")
	@TableField("name")
	private String name;

	@ApiModelProperty(value = "父分类id")
	@TableField("parent_id")
	private Integer parentId;

	@ApiModelProperty(value = "分类描述")
	@TableField("description")
	private String description;

}
