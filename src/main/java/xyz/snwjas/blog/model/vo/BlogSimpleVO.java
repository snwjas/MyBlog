package xyz.snwjas.blog.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import xyz.snwjas.blog.model.base.BeanConvert;
import xyz.snwjas.blog.model.base.ValidGroupType.Save;
import xyz.snwjas.blog.model.base.ValidGroupType.Update;
import xyz.snwjas.blog.model.enums.BlogCommentAllowStatus;
import xyz.snwjas.blog.model.enums.BlogStatus;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 博客列表View Object
 *
 * @author Myles Yang
 */
@Data
@Accessors(chain = true)
@ApiModel("简单的博客文章视图对象")
public class BlogSimpleVO implements BeanConvert, Serializable {

	private static final long serialVersionUID = 6349521681640492470L;

	@NotNull(groups = Update.class, message = "博客id不能为空")
	@Min(value = 1, groups = Update.class)
	@ApiModelProperty("文章ID")
	private Integer id;

	@Length(max = 255, groups = {Save.class, Update.class})
	@ApiModelProperty("文章标题")
	private String title;

	@Length(max = 255, groups = {Save.class, Update.class})
	@ApiModelProperty("文章访问URL")
	private String url;

	@Length(max = 511, groups = {Save.class, Update.class})
	@ApiModelProperty("文章概要")
	private String summary;

	@Length(max = 1023, groups = {Save.class, Update.class})
	@ApiModelProperty("文章缩略图")
	private String thumbnail;

	@ApiModelProperty("文章置顶数值（数值越大越靠前）")
	private Integer topRank;

	@ApiModelProperty("文章访问数量")
	private Integer visits;

	@ApiModelProperty("文章状态")
	private BlogStatus status;

	@ApiModelProperty("文章分类")
	private CategoryVO category;

	@ApiModelProperty("文章是否允许评论")
	private BlogCommentAllowStatus allowComment;

	private LocalDateTime createTime;

	private LocalDateTime updateTime;

	@ApiModelProperty("评论数量")
	private Integer commentCount;

	@ApiModelProperty("文章标签列表")
	private List<TagVO> tags;

}
