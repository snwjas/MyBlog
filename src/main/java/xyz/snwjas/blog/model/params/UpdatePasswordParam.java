package xyz.snwjas.blog.model.params;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * Update Password Param
 *
 * @author Myles Yang
 */
@Data
@ApiModel("密码更新参数")
public class UpdatePasswordParam {

	@NotBlank
	@Length(max = 63)
	@ApiModelProperty("原密码")
	private String oldPassword;

	@NotBlank
	@Length(max = 63)
	@ApiModelProperty("新密码")
	private String newPassword;

	@NotBlank
	@Length(max = 63)
	@ApiModelProperty("确认的新密码")
	private String confirmNewPassword;

}
