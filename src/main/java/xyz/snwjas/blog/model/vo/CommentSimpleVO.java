package xyz.snwjas.blog.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import xyz.snwjas.blog.model.base.BeanConvert;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Comment VO
 *
 * @author Myles Yang
 */
@Data
@Accessors(chain = true)
@ApiModel("简单的评论视图对象")
public class CommentSimpleVO implements BeanConvert, Serializable {

	private static final long serialVersionUID = -7923533144012981489L;

	@ApiModelProperty("评论ID")
	private Integer id;

	@NotEmpty(groups = {Admin.class, Guest.class}, message = "评论内容不能为空")
	@Length(max = 1023, groups = {Admin.class, Guest.class}, message = "评论内容长度请控制在1023个字符")
	@ApiModelProperty("评论内容")
	private String content;

	@NotEmpty(groups = {Guest.class}, message = "昵称不能为空")
	@Length(max = 63, groups = {Admin.class, Guest.class}, message = "昵称长度不能超过63个字符")
	@ApiModelProperty("评论者昵称")
	private String author;

	@Min(value = 0, groups = {Admin.class, Guest.class})
	@ApiModelProperty("父评论ID")
	private Integer parentId;

	@NotNull(groups = {Admin.class, Guest.class}, message = "未知博客的评论")
	@Min(value = 1, groups = {Admin.class, Guest.class}, message = "未知博客的评论")
	@ApiModelProperty("评论所属博客文章ID")
	private Integer blogId;

	@ApiModelProperty("评论者的用户代理")
	private String userAgent;

	@ApiModelProperty("评论是否是博主发表的")
	private Boolean isAdmin;

	@ApiModelProperty(value = "评论作者头像")
	private String avatar;

	private LocalDateTime createTime;

	@ApiModelProperty("子评论数量")
	private Integer childrenCount;

	@ApiModelProperty("子评论")
	private List<CommentSimpleVO> children;

	// 管理员校验
	public interface Admin {
	}

	// 游客校验
	public interface Guest {
	}

}
