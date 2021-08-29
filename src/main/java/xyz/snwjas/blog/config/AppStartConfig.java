package xyz.snwjas.blog.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.util.StringUtils;
import xyz.snwjas.blog.constant.MyBlogOptionEnum;
import xyz.snwjas.blog.constant.OptionEnum;
import xyz.snwjas.blog.model.vo.OptionVO;
import xyz.snwjas.blog.service.OptionsService;
import xyz.snwjas.blog.utils.ClassUtils;

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
			for (Class<OptionEnum> clazz : ClassUtils.getAllClassByInterface(OptionEnum.class)) {
				for (OptionEnum oenum : clazz.getEnumConstants()) {
					if (StringUtils.hasText(oenum.key())) {
						String value = oenum.defaultValue() == null ? "" : oenum.defaultValue();
						optionsService.setIfAbsence(oenum.key(), value);
					}
				}
			}
			log.info("首次启动应用，已初始化设置");
		}
	}

}
