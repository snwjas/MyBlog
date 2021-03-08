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
 * 博客标签
 * </p>
 *
 * @author Myles Yang
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("blog_tag")
@ApiModel(value = "BlogTagEntity对象", description = "博客标签")
public class BlogTagEntity extends BaseEntity {

	private static final long serialVersionUID = 437792569342041771L;

	@ApiModelProperty(value = "博客标签id")
	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;

	@ApiModelProperty(value = "标签id")
	@TableField("tag_id")
	private Integer tagId;

	@ApiModelProperty(value = "博客id")
	@TableField("blog_id")
	private Integer blogId;

}
