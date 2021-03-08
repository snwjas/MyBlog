package xyz.snwjas.blog.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import xyz.snwjas.blog.model.base.BeanConvert;

import java.io.Serializable;

/**
 * 博客列表View Object
 *
 * @author Myles Yang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@ApiModel("详细的博客文章视图对象")
public class BlogDetailVO extends BlogSimpleVO implements BeanConvert, Serializable {

	private static final long serialVersionUID = 4692344576356025171L;

	@ApiModelProperty("源格式文本（Markdown）")
	private String originalContent;

	@ApiModelProperty("格式化文本（HTML）")
	private String formatContent;

	@ApiModelProperty("点赞数量")
	private Integer likes;

}
