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
 * 用户
 * </p>
 *
 * @author Myles Yang
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("user")
@ApiModel(value = "UserEntity对象", description = "用户")
public class UserEntity extends BaseEntity {

	private static final long serialVersionUID = 1257044767231325225L;

	@ApiModelProperty(value = "用户id")
	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;

	@ApiModelProperty(value = "登录用户名")
	@TableField("username")
	private String username;

	@ApiModelProperty(value = "登录密码")
	@TableField("password")
	private String password;

	@ApiModelProperty(value = "用户昵称")
	@TableField("nickname")
	private String nickname;

	@ApiModelProperty(value = "用户邮箱")
	@TableField("email")
	private String email;

	@ApiModelProperty(value = "用户头像链接")
	@TableField("avatar")
	private String avatar;

	@ApiModelProperty(value = "个人描述")
	@TableField("description")
	private String description;

}
