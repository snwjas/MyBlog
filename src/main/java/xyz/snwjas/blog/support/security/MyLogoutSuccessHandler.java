package xyz.snwjas.blog.support.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import xyz.snwjas.blog.annotation.ActionRecord;
import xyz.snwjas.blog.model.R;
import xyz.snwjas.blog.model.enums.LogType;
import xyz.snwjas.blog.utils.IPUtils;
import xyz.snwjas.blog.utils.RUtils;
import xyz.snwjas.blog.utils.RWriterUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 注销成功处理
 */
@Component
@Slf4j
public class MyLogoutSuccessHandler implements LogoutSuccessHandler {

	@ActionRecord(value = "#authentication.getName()", type = LogType.LOGGED_OUT)
	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response
			, Authentication authentication) throws IOException, ServletException {
		log.info("用户 [{}] 注销登录，IP：[{}]", authentication.getName(), IPUtils.getIpAddress(request));
		R result = RUtils.success("登录已注销");
		RWriterUtils.writeJson(response, result);
	}
}
