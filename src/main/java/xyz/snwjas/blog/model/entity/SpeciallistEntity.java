package xyz.snwjas.blog.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import xyz.snwjas.blog.model.enums.SpecialListType;

/**
 * <p>
 * 特殊名单
 * </p>
 *
 * @author Myles Yang
 */
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("speciallist")
@ApiModel(value = "SpeciallistEntity对象", description = "特殊名单表")
public class SpeciallistEntity extends BaseEntity {

	private static final long serialVersionUID = -8025860806817409251L;

	@ApiModelProperty(value = "id")
	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;

	@ApiModelProperty(value = "类型")
	@TableField("type")
	private SpecialListType type;

	@ApiModelProperty(value = "内容")
	@TableField("content")
	private String content;

	public SpeciallistEntity(SpecialListType type, String content) {
		this.type = type;
		this.content = content;
	}

}
