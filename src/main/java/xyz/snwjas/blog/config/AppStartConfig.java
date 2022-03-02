package xyz.snwjas.blog.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.util.StringUtils;
import xyz.snwjas.blog.constant.MyBlogOptionEnum;
import xyz.snwjas.blog.constant.OptionEnum;
import xyz.snwjas.blog.model.enums.SpecialListType;
import xyz.snwjas.blog.model.vo.OptionVO;
import xyz.snwjas.blog.model.vo.SpeciallistVO;
import xyz.snwjas.blog.service.OptionsService;
import xyz.snwjas.blog.service.SpeciallistService;
import xyz.snwjas.blog.support.wordfilter.WordFilter;
import xyz.snwjas.blog.support.wordfilter.WordType;
import xyz.snwjas.blog.utils.ClassUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

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

	@Autowired
	private SpeciallistService speciallistService;

	@Autowired
	private WordFilter wordFilter;


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

	/**
	 * 初始化敏感词过滤器词条
	 */
	@EventListener(ApplicationReadyEvent.class)
	public void initWordFilter() throws Exception {
		Object PRESENT = new Object();
		Map<String, Object> blackList = new ConcurrentHashMap<>(128);
		Map<String, Object> whiteList = new ConcurrentHashMap<>(32);
		List<SpeciallistVO> list = speciallistService.listAll(SpecialListType.WORD_BLACK_LIST, SpecialListType.WORD_WHITE_LIST);
		list.stream().parallel().forEach(o -> {
			if (Objects.nonNull(o.getType())) {
				if (SpecialListType.WORD_BLACK_LIST.equals(o.getType())) {
					blackList.put(o.getContent(), PRESENT);
				} else if (SpecialListType.WORD_WHITE_LIST.equals(o.getType())) {
					whiteList.put(o.getContent(), PRESENT);
				}
			}
		});
		wordFilter.getContext().addWord(blackList.keySet(), WordType.BLACK);
		wordFilter.getContext().addWord(whiteList.keySet(), WordType.WHITE);
		log.info("加载敏感词词条完毕：黑名单 {} 条，白名单 {} 条", blackList.size(), whiteList.size());
	}

}
