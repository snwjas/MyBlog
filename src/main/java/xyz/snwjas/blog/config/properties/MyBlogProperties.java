package xyz.snwjas.blog.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.File;

/**
 * 博客配置
 *
 * @author Myles Yang
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "my-blog")
public class MyBlogProperties {
	/**
	 * swagger doc
	 */
	private boolean docEnable = false;

	/**
	 * 后台管理入口，不需要添加'/'
	 */
	private String adminPath = "admin";

	/**
	 * 允许连续登录失败的时间（单位秒）
	 */
	private int allowLoginFailureSeconds = 3600;

	/**
	 * 允许登录失败的次数
	 */
	private int allowLoginFailureCount = 10;


	/**
	 * 登录记住我token时间(单位秒，1周 = 604800秒)
	 */
	private int rememberMeTokenValiditySeconds = 604800;

	/**
	 * 上传文件保存路径
	 */
	private String fileSavePath = System.getProperty("user.home")
			+ File.separatorChar + "MyBlog" + File.separatorChar + "files";

}
