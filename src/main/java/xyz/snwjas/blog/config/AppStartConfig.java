package xyz.snwjas.blog.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import xyz.snwjas.blog.constant.MyBlogOptionEnum;
import xyz.snwjas.blog.model.vo.OptionVO;
import xyz.snwjas.blog.service.OptionsService;

import java.util.Objects;

/**
 * 应用启动后的配置
 *
 * @author Myles Yang
 */
@Configuration
@Slf4j
public class AppStartConfig {

	@Autowired
	private OptionsService optionsService;

	/**
	 * 博客初始化
	 */
	@EventListener(ApplicationReadyEvent.class)
	public void initialization() {
		OptionVO birthday = optionsService.get(MyBlogOptionEnum.BIRTHDAY.key());
		// 第一次启动应用
		if (Objects.isNull(birthday)) {
			for (MyBlogOptionEnum value : MyBlogOptionEnum.values()) {
				optionsService.setIfAbsence(value.key(), value.defaultValue());
			}
			log.info("首次启动应用，已初始化设置");
		}
	}

}
