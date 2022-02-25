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
import xyz.snwjas.blog.model.enums.CommentStatus;

/**
 * <p>
 * 评论
 * </p>
 *
 * @author Myles Yang
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("comment")
@ApiModel(value = "CommentEntity对象", description = "评论")
public class CommentEntity extends BaseEntity {

	private static final long serialVersionUID = -9064254063786654890L;

	@ApiModelProperty(value = "评论id")
	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;

	@ApiModelProperty(value = "父评论id")
	@TableField("parent_id")
	private Integer parentId;

	@ApiModelProperty(value = "评论内容")
	@TableField("content")
	private String content;

	@ApiModelProperty(value = "评论作者")
	@TableField("author")
	private String author;

	@ApiModelProperty(value = "评论作者邮箱")
	@TableField("email")
	private String email;

	@ApiModelProperty(value = "评论作者头像")
	@TableField("avatar")
	private String avatar;

	@ApiModelProperty(value = "评论作者的ip地址")
	@TableField("ip_address")
	private Integer ipAddress;

	@ApiModelProperty(value = "评论作者的用户代理")
	@TableField("user_agent")
	private String userAgent;

	@ApiModelProperty(value = "博客id")
	@TableField("blog_id")
	private Integer blogId;

	@ApiModelProperty(value = "评论状态")
	@TableField("status")
	private CommentStatus status;

	@ApiModelProperty(value = "是否是管理员评论，默认false(0)")
	@TableField("is_admin")
	private Boolean isAdmin;

}
