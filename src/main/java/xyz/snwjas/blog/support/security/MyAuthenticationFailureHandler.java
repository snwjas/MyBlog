package xyz.snwjas.blog.support.security;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import xyz.snwjas.blog.annotation.ActionRecord;
import xyz.snwjas.blog.constant.RS;
import xyz.snwjas.blog.model.R;
import xyz.snwjas.blog.model.enums.LogType;
import xyz.snwjas.blog.utils.RUtils;
import xyz.snwjas.blog.utils.RWriterUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 认证失败处理
 */
@Component
public class MyAuthenticationFailureHandler implements AuthenticationFailureHandler {

	@ActionRecord(content = "#exception.getMessage()", type = LogType.LOGIN_FAILED)
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response
			, AuthenticationException exception) throws IOException, ServletException {
		R result = RUtils.fail(exception instanceof BadCredentialsException
				? RS.USERNAME_PASSWORD_ERROR
				: RS.SYSTEM_ERROR);
		RWriterUtils.writeJson(response, result);
	}
}
