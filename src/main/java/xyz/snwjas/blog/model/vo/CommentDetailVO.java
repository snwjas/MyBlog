package xyz.snwjas.blog.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import xyz.snwjas.blog.model.enums.CommentStatus;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

/**
 * Comment VO
 *
 * @author Myles Yang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
@Accessors(chain = true)
@ApiModel("详细的评论视图对象")
public class CommentDetailVO extends CommentSimpleVO {

	private static final long serialVersionUID = 1080754350404773083L;

	@NotEmpty(groups = {Guest.class}, message = "邮箱不能为空")
	@Email(groups = {Guest.class}, message = "邮箱格式错误")
	@Length(max = 127, groups = {Admin.class, Guest.class}, message = "邮箱长度不能超过127个字符")
	@ApiModelProperty("评论者邮件地址")
	private String email;

	@ApiModelProperty("评论者IP地址")
	private String ipAddress;

	@ApiModelProperty("评论状态")
	private CommentStatus status;

	private String blogTitle;

	private String blogUrl;

}
