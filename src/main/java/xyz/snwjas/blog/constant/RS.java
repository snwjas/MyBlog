package xyz.snwjas.blog.constant;

import org.springframework.http.HttpStatus;

/**
 * 自定义状态码（Response Status）
 * <p>
 * 200 成功
 * 1001 - 1999 参数
 * 2001 - 2999 用户
 * 3001 - 3999 权限
 * 4001 - 4999 数据
 * 5001 - 5999 业务
 * 6001 - 6999 接口
 * 7001 - 7999 系统
 *
 * @author Myles Yang
 */
public enum RS {

	SUCCESS(HttpStatus.OK.value(), "成功"),

	/**
	 * 参数错误
	 */
	ILLEGAL_PARAMETER(1001, "非法参数"),

	FILE_SIZE_LIMIT(1002, "上传文件过大"),

	/**
	 * 用户错误
	 */
	USERNAME_PASSWORD_ERROR(2001, "用户名或密码错误"),

	USERNAME_ERROR(2002, "用户名错误"),

	PASSWORD_ERROR(2003, "密码错误"),

	INCONSISTENT_PASSWORDS(2004, "两次输入的密码不一致"),

	MULTIPLE_AUTHENTICATION_FAILURE(2005, "多次认证失败"),

	/**
	 * 权限
	 */
	NOT_LOGIN(3001, "用户未登录"),


	/**
	 * 接口错误
	 */
	FREQUENT_OPERATION(6001, "操作过于频繁，请稍后再试"),

	METHOD_NOT_SUPPORTED(6002, "请求方法有误"),

	PAGE_NOT_FOUND(6003, "请求目标不存在"),

	/**
	 * 系统错误
	 */
	SYSTEM_ERROR(7001, "系统错误"),

	;

	private final int status;

	private final String message;

	RS(int status, String message) {
		this.status = status;
		this.message = message;
	}

	public int status() {
		return status;
	}

	public String message() {
		return message;
	}

	public static RS resolve(int statusCode) {
		for (RS rc : values()) {
			if (rc.status == statusCode) {
				return rc;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return "{\"status\":" + status + ",\"message\":\"" + message + "\"}";
	}
}
