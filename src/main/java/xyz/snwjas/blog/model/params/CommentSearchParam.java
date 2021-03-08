package xyz.snwjas.blog.model.params;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import xyz.snwjas.blog.model.enums.CommentStatus;

/**
 * Common Search Param
 *
 * @author Myles Yang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@ApiModel("评论搜索参数")
public class CommentSearchParam extends BasePageParam {

	@ApiModelProperty("评论者昵称")
	private String author;

	@ApiModelProperty("评论者昵称")
	private String content;

	@ApiModelProperty("评论者邮箱地址")
	private String email;

	@ApiModelProperty("关键字：author || content || email")
	private String keyword; // author || content || email

	@ApiModelProperty("评论所属博客文章ID")
	private Integer blogId;

	@ApiModelProperty("评论状态")
	private CommentStatus status;

}
