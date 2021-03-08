package xyz.snwjas.blog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.filter.CharacterEncodingFilter;
import xyz.snwjas.blog.config.properties.MyBlogProperties;
import xyz.snwjas.blog.support.security.MyAuthenticationFailureHandler;
import xyz.snwjas.blog.support.security.MyAuthenticationSuccessHandler;
import xyz.snwjas.blog.support.security.MyLogoutSuccessHandler;
import xyz.snwjas.blog.support.security.MyUserDetailsService;

/**
 * Spring Security 配置
 *
 * @author Myles Yang
 */
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private MyBlogProperties properties;

	@Autowired
	private MyUserDetailsService myUserDetailsService;

	@Autowired
	private MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;

	@Autowired
	private MyAuthenticationFailureHandler myAuthenticationFailureHandler;

	@Autowired
	private MyLogoutSuccessHandler myLogoutSuccessHandler;

	/**
	 * 密码加密器
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		// the version of bcrypt, can be 2a,2b,2y
		// the log rounds to use, between 4 and 31
		return new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2Y, 5);
	}

	/**
	 * 自定义登录业务逻辑配置
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(myUserDetailsService);
	}

	/**
	 * Http 安全配置
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		// 允许来自同一域的任何请求不被添加到响应中
		http.headers().frameOptions().sameOrigin().httpStrictTransportSecurity().disable();

		// 关闭csrf，允许跨域访问
		http.csrf().disable().cors();

		// 放行 与 拦截 的请求
		http.authorizeRequests()
				.antMatchers("/api/admin/user/login", "/api/admin/user/checkLogin").permitAll()
				.antMatchers("/api/admin/**").authenticated();

		// 登录表单验证处理
		http.formLogin()
				.loginPage("/api/admin/user/login")
				.usernameParameter("username")
				.passwordParameter("password")
				// 认证成功处理
				.successHandler(myAuthenticationSuccessHandler)
				// 认证失败处理
				.failureHandler(myAuthenticationFailureHandler);

		// 注销
		http.logout()
				.logoutUrl("/api/admin/user/logout")
				.deleteCookies("JSESSIONID", "rememberMe")
				.invalidateHttpSession(true)
				// 注销成功处理
				.logoutSuccessHandler(myLogoutSuccessHandler);

		// 开启登录的记住我功能，SpringSecurity中token默认有效期为两周
		// checkbox 支持校验的值["true", "on", "yes", "1"]
		// 具体参考 {@link AbstractRememberMeServices#rememberMeRequested(HttpServletRequest, String)}
		http.rememberMe()
				.rememberMeParameter("rememberMe")
				.tokenValiditySeconds(properties.getRememberMeTokenValiditySeconds());

		// 编码过滤器
		CharacterEncodingFilter enf = new CharacterEncodingFilter("UTF-8", false, true);
		http.addFilterBefore(enf, CsrfFilter.class);

	}

}
