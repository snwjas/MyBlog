package xyz.snwjas.blog.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import xyz.snwjas.blog.config.properties.MyBlogProperties;

import java.time.temporal.Temporal;

/**
 * Swagger 配置
 *
 * @author Myles Yang
 */
@Configuration
@EnableSwagger2
@Slf4j
public class SwaggerConfig {

	private final MyBlogProperties properties;

	public SwaggerConfig(MyBlogProperties properties) {
		this.properties = properties;
	}

	@Bean
	public Docket docket() {
		if (!properties.isDocEnable()) {
			log.debug("Swagger Doc has been disabled.");
		}
		return buildDocket("Default",
				"xyz.snwjas.blog.controller",
				"/**")
				.enable(properties.isDocEnable());
	}

	private Docket buildDocket(String groupName, String basePackage, String antPattern) {
		return new Docket(DocumentationType.SWAGGER_2)
				.groupName(groupName)
				.select()
				.apis(RequestHandlerSelectors.basePackage(basePackage))
				.paths(PathSelectors.ant(antPattern))
				.build()
				.apiInfo(apiInfo())
				.directModelSubstitute(Temporal.class, String.class);

	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
				.title("MyBlog API Documentation.")
				.description("The API documentation for MyBlog.")
				.version("1.0.0")
				.contact(new Contact("Myles Yang", "snwjas.xyz", "myles.yang@foxmail.com"))
				.license("MIT")
				.licenseUrl("")
				.build();
	}
}
