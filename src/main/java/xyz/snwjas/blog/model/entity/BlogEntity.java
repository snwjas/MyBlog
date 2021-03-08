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
import xyz.snwjas.blog.model.enums.BlogCommentAllowStatus;
import xyz.snwjas.blog.model.enums.BlogStatus;

/**
 * <p>
 * 博客
 * </p>
 *
 * @author Myles Yang
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("blog")
@ApiModel(value = "BlogEntity对象", description = "博客")
public class BlogEntity extends BaseEntity {

	private static final long serialVersionUID = 2193309050912718282L;

	@ApiModelProperty(value = "博客id")
	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;

	@ApiModelProperty(value = "博客标题")
	@TableField("title")
	private String title;

	@ApiModelProperty(value = "原格式博客内容")
	@TableField("original_content")
	private String originalContent;

	@ApiModelProperty(value = "格式化(html)博客内容")
	@TableField("format_content")
	private String formatContent;

	@ApiModelProperty(value = "博客链接")
	@TableField("url")
	private String url;

	@ApiModelProperty(value = "博客摘要")
	@TableField("summary")
	private String summary;

	@ApiModelProperty(value = "博客缩略图链接")
	@TableField("thumbnail")
	private String thumbnail;

	@ApiModelProperty(value = "博客置顶排行")
	@TableField("top_rank")
	private Integer topRank;

	@ApiModelProperty(value = "是否允许评论，默认(1，允许但需审核)")
	@TableField("allow_comment")
	private BlogCommentAllowStatus allowComment;

	@ApiModelProperty(value = "喜欢的人数")
	@TableField("likes")
	private Integer likes;

	@ApiModelProperty(value = "博客访问人数")
	@TableField("visits")
	private Integer visits;

	@ApiModelProperty(value = "博客状态")
	@TableField("status")
	private BlogStatus status;

	@ApiModelProperty(value = "博客分类ID")
	@TableField("category_id")
	private Integer categoryId;

}
