package xyz.snwjas.blog.controller.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import xyz.snwjas.blog.constant.ResponseStatus;
import xyz.snwjas.blog.model.params.UpdatePasswordParam;
import xyz.snwjas.blog.model.vo.UserVO;
import xyz.snwjas.blog.service.UserService;
import xyz.snwjas.blog.model.R;
import xyz.snwjas.blog.model.UserDetail;
import xyz.snwjas.blog.support.cache.MemoryCacheStore;
import xyz.snwjas.blog.utils.RUtils;

import java.util.HashMap;
import java.util.Objects;

/**
 * User Controller
 *
 * @author Myles Yang
 */
@RestController("AdminUserController")
@RequestMapping("/api/admin/user")
@Api(value = "后台用户控制器", tags = {"后台用户接口"})
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private MemoryCacheStore cache;

	@ApiOperation("检查用户登录状态")
	@GetMapping("/login")
	public R login() {
		Authentication auth = userService.getAuth();
		if (Objects.isNull(auth)) {
			return RUtils.fail(ResponseStatus.NOT_LOGIN);
		}
		return RUtils.success("用户 [" + auth.getName() + "] 已登录");
	}

	@ApiOperation("检查用户登录状态")
	@GetMapping("/checkLogin")
	public R checkLogin() {
		Authentication auth = userService.getAuth();
		HashMap<String, Object> data = new HashMap<>(4);
		if (Objects.isNull(auth)) {
			data.put("isLogin", false);
		} else {
			data.put("isLogin", true);
			data.put("user", auth.getName());
		}
		return RUtils.success("用户登录状态", data);
	}

	@ApiOperation("获取用户信息")
	@GetMapping("/info")
	public R info() {
		UserDetail userDetails = getUserDetail();
		UserVO userVO = new UserVO().convertFrom(userDetails);
		return RUtils.success("用户信息", userVO);
	}

	@ApiOperation("更新用户信息")
	@PostMapping("/update/profile")
	public R updateProfile(@RequestBody @Validated UserVO userVO) {
		int userId = getUserDetail().getId();
		int i = userService.updateProfile(userId, userVO);
		if (i > 0) {
			UserDetail userDetail = userService.getByUserId(userId);
			// userDetail.convertFrom(userDetail); // 不行
			updateSecurityContextUserDetail(userDetail);
			return RUtils.success("用户信息更新成功", userDetail.convertTo(new UserVO()));
		}
		return RUtils.fail("用户信息更新失败");
	}

	@ApiOperation("更新用户密码")
	@PostMapping("/update/password")
	public R updatePassword(@RequestBody @Validated UpdatePasswordParam param) {
		String newPassword = param.getNewPassword();
		String confirmNewPassword = param.getConfirmNewPassword();
		if (!newPassword.equals(confirmNewPassword)) {
			return RUtils.fail(ResponseStatus.INCONSISTENT_PASSWORDS);
		}

		UserDetail userDetail = getUserDetail();
		if (!passwordEncoder.matches(param.getOldPassword(), userDetail.getPassword())) {
			return RUtils.fail(ResponseStatus.PASSWORD_ERROR);
		}

		String encodedPassword = passwordEncoder.encode(newPassword);
		int i = userService.updatePassword(userDetail.getId(), encodedPassword);
		if (i > 0) {
			userDetail.setPassword(encodedPassword);
			return RUtils.success("密码更新成功");
		}

		return RUtils.fail("密码更新失败");
	}

	// 获取 SpringSecurity 中认证的用户信息
	private UserDetail getUserDetail() {
		Authentication auth = userService.getAuth();
		return (UserDetail) auth.getPrincipal();
	}

	// 更新 SpringSecurity 中认证的用户信息
	private void updateSecurityContextUserDetail(UserDetail userDetail) {
		Authentication auth = userService.getAuth();
		UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken(
				userDetail, auth.getCredentials(), auth.getAuthorities());
		upat.setDetails(auth.getDetails());
		SecurityContextHolder.getContext().setAuthentication(upat);
	}

}
