package xyz.snwjas.blog.model.params;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import xyz.snwjas.blog.model.enums.BlogStatus;

/**
 * Blog Search Param
 *
 * @author Myles Yang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@ApiModel("博客搜索参数")
public class BlogSearchParam extends BasePageParam {

	@ApiModelProperty("博客文章标题")
	private String title;

	@ApiModelProperty("博客分类ID")
	private Integer categoryId;

	@ApiModelProperty("博客标签ID")
	private Integer tagId;

	@ApiModelProperty("博客状态")
	private BlogStatus status;

}
