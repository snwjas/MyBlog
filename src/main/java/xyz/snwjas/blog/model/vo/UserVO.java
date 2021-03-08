package xyz.snwjas.blog.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import xyz.snwjas.blog.model.base.BeanConvert;

import javax.validation.constraints.Email;
import java.io.Serializable;

/**
 * User VO
 *
 * @author Myles Yang
 */
@Data
@ApiModel("用户视图对象")
public class UserVO implements BeanConvert, Serializable {

	private static final long serialVersionUID = -2053049610535340848L;

	@Length(max = 63)
	@ApiModelProperty("用户名")
	private String username;

	@Length(max = 127)
	@ApiModelProperty("用户昵称")
	private String nickname;

	@Email
	@Length(max = 127)
	@ApiModelProperty("用户邮箱地址")
	private String email;

	@Length(max = 1023)
	@ApiModelProperty("用户头像地址")
	private String avatar;

	@Length(max = 1023)
	@ApiModelProperty("用户描述")
	private String description;

}
