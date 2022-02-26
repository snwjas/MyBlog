package xyz.snwjas.blog.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import xyz.snwjas.blog.config.properties.MyBlogProperties;
import xyz.snwjas.blog.constant.RS;
import xyz.snwjas.blog.interceptor.AccessLimitInterceptor;
import xyz.snwjas.blog.interceptor.StatisticInterceptor;
import xyz.snwjas.blog.utils.FileUtils;
import xyz.snwjas.blog.utils.RUtils;
import xyz.snwjas.blog.utils.RWriterUtils;

import java.util.List;

/**
 * WebMvc配置
 *
 * @author Myles Yang
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

	@Autowired
	private MyBlogProperties properties;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private AccessLimitInterceptor accessLimitInterceptor;

	@Autowired
	private StatisticInterceptor statisticInterceptor;

	/**
	 * 监听HTTP请求事件
	 * 解决 RequestContextHolder.getRequestAttributes() 空指针问题
	 */
	@Bean
	public RequestContextListener requestContextListener() {
		return new RequestContextListener();
	}

	/**
	 * 跨域访问配置
	 */
	@Bean
	@Profile({"dev", "test"})
	public CorsFilter corsFilter() {
		CorsConfiguration config = new CorsConfiguration();
		config.addAllowedOrigin("*");
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		config.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}

	/**
	 * 自定义json消息转换器
	 */
	@Bean
	public MappingJackson2HttpMessageConverter customJackson2HttpMessageConverter() {
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setObjectMapper(objectMapper);
		return converter;
	}

	@Override
	protected void addResourceHandlers(ResourceHandlerRegistry registry) {

		// swagger-ui doc enable
		if (properties.isDocEnable()) {
			registry.addResourceHandler("swagger-ui.html")
					.addResourceLocations("classpath:/META-INF/resources/");
			registry.addResourceHandler("/webjars/**")
					.addResourceLocations("classpath:/META-INF/resources/webjars/");
		}

		// 后台资源映射
		String adminPath = "/" + properties.getAdminPath() + "/**";
		String adminWebPath = StringUtils.hasText(properties.getAdminWebPath())
				? FileUtils.getFileResLoc(properties.getAdminWebPath())
				: "classpath:/admin/";
		registry.addResourceHandler(adminPath)
				.addResourceLocations(adminWebPath);
		// 前台资源映射
		String appWebPath = StringUtils.hasText(properties.getAppWebPath())
				? FileUtils.getFileResLoc(properties.getAppWebPath())
				: "classpath:/app/";
		registry.addResourceHandler("/app/**")
				.addResourceLocations(appWebPath);

		// 上传文件的静态资源映射
		registry.addResourceHandler("/static/**"/*, "/favicon.ico"*/)
				.addResourceLocations("classpath:/static/", FileUtils.getFileResLoc(properties.getFileSavePath()));

		// super.addResourceHandlers(registry);
	}

	/**
	 * 拦截器
	 */
	@Override
	protected void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(accessLimitInterceptor);
		registry.addInterceptor(statisticInterceptor)
				.excludePathPatterns("/api/**", "/" + properties.getAdminPath() + "/**");
		super.addInterceptors(registry);
	}

	/**
	 * 配置消息转换器
	 */
	@Override
	protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(customJackson2HttpMessageConverter());
		super.configureMessageConverters(converters);
	}

	/**
	 * error page 返回json
	 */
	@Bean("error")
	public View error() {
		// return new MappingJackson2JsonView();
		return (model, request, response) -> {
			RWriterUtils.writeJson(response, RUtils.fail(RS.PAGE_NOT_FOUND, model.get("path")));
		};
	}
}
