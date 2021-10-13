package xyz.snwjas.blog.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import xyz.snwjas.blog.annotation.ActionRecord;
import xyz.snwjas.blog.constant.MyBlogOptionEnum;
import xyz.snwjas.blog.constant.OptionEnum;
import xyz.snwjas.blog.mapper.OptionsMapper;
import xyz.snwjas.blog.model.entity.OptionsEntity;
import xyz.snwjas.blog.model.enums.LogType;
import xyz.snwjas.blog.model.vo.OptionVO;
import xyz.snwjas.blog.service.OptionsService;
import xyz.snwjas.blog.support.cache.MemoryCacheStore;
import xyz.snwjas.blog.utils.ClassUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Options Service
 *
 * @author Myles Yang
 */
@Service
public class OptionsServiceImpl implements OptionsService {

	@Resource
	private OptionsMapper optionsMapper;

	@Autowired
	private MemoryCacheStore cache;

	private static final String OPTIONS_CACHE_KEY = "options";

	private static final int OPTIONS_CACHE_TIMEOUT = 86400;

	private static final String OPTIONS_KEY_TYPE_MAP = "options_key_type_map";


	@Override
	public OptionVO get(String key) {
		Assert.hasText(key, "key must be had text.");
		OptionsEntity optionsEntity = optionsMapper.selectOne(
				Wrappers.lambdaQuery(OptionsEntity.class)
						.eq(OptionsEntity::getOptionKey, key)
		);
		return covertToVO(optionsEntity);
	}

	@ActionRecord(content = "'key: '+#key+', value: '+#value", type = LogType.OPTION_UPDATE)
	@Override
	public int setAnyway(String key, String value) {
		Assert.hasText(key, "key must be had text.");
		OptionVO optionVO = get(key);
		value = value == null ? "" : value;
		if (Objects.isNull(optionVO)) { // add
			OptionsEntity optionsEntity = new OptionsEntity()
					.setOptionKey(key)
					.setOptionValue(value);
			optionsMapper.insert(optionsEntity);
			return optionsEntity.getId();
		} else { // update
			optionsMapper.update(null,
					Wrappers.lambdaUpdate(OptionsEntity.class)
							.eq(OptionsEntity::getId, optionVO.getId())
							.set(OptionsEntity::getOptionValue, value)
			);
		}
		return optionVO.getId();
	}

	@ActionRecord(content = "'key: '+#key+', value: '+#value",
			type = LogType.OPTION_UPDATE,
			condition = "#ret > 0")
	@Override
	public int setIfAbsence(String key, String value) {
		Assert.hasText(key, "key must be had text.");
		if (Objects.isNull(get(key))) {
			OptionsEntity optionsEntity = new OptionsEntity()
					.setOptionKey(key)
					.setOptionValue(Objects.isNull(value) ? "" : value);
			optionsMapper.insert(optionsEntity);
			return optionsEntity.getId();
		}
		return -1;
	}

	@ActionRecord(content = "'key: '+#key+', value: '+#value",
			type = LogType.OPTION_UPDATE,
			condition = "#ret > 0")
	@Override
	public int setIfPresent(String key, String value) {
		Assert.hasText(key, "key must be had text.");
		OptionVO optionVO = get(key);
		if (Objects.nonNull(optionVO)) {
			return optionsMapper.update(null,
					Wrappers.lambdaUpdate(OptionsEntity.class)
							.eq(OptionsEntity::getId, optionVO.getId())
							.set(OptionsEntity::getOptionValue, Objects.isNull(value) ? "" : value));
		}
		return -1;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, Object> listAll() {
		Map<String, Object> optionMap = (Map<String, Object>) cache.get(OPTIONS_CACHE_KEY);
		if (Objects.isNull(optionMap)) {
			synchronized (this) {
				optionMap = (Map<String, Object>) cache.get(OPTIONS_CACHE_KEY);
				if (Objects.isNull(optionMap)) {
					List<OptionsEntity> allOptions = optionsMapper.selectList(Wrappers.emptyWrapper());
					Map<String, Class<?>> optionKeyTypeMap = getOptionEnumMap();
					optionMap = new HashMap<>();
					for (OptionsEntity option : allOptions) {
						String optionKey = option.getOptionKey();
						Class<?> aClass = optionKeyTypeMap.get(optionKey);
						if (Objects.isNull(aClass)) {
							optionMap.put(optionKey, option.getOptionValue());
							continue;
						}
						Object optionValue = OptionEnum.getTrueOptionValue(option.getOptionValue(), aClass);
						optionMap.put(optionKey, optionValue);
					}
					cache.set(OPTIONS_CACHE_KEY, optionMap, OPTIONS_CACHE_TIMEOUT);
				}
			}
		}
		return optionMap;
	}

	@Override
	public Map<String, Object> listNecessary() {
		HashMap<String, Object> map = new HashMap<>(4);

		MyBlogOptionEnum[] oes = {
				MyBlogOptionEnum.NAME, MyBlogOptionEnum.URL, MyBlogOptionEnum.DESCRIPTION
				, MyBlogOptionEnum.LOGO, MyBlogOptionEnum.FAVICON
		};

		for (MyBlogOptionEnum oe : oes) {
			OptionVO vo = get(oe.key());
			map.put(oe.key(), Objects.isNull(vo) ? oe.defaultValue() : vo.getOptionValue());
		}

		return map;
	}

	@Override
	public boolean resetOptionsCache() {
		return cache.delete(OPTIONS_CACHE_KEY);
	}

	@Override
	public OptionVO covertToVO(OptionsEntity optionsEntity) {
		return new OptionVO().convertFrom(optionsEntity);
	}

	// option key as map key
	// 获取所有已配置设置项的数据类型
	@SuppressWarnings("unchecked")
	private Map<String, Class<?>> getOptionEnumMap() {
		HashMap<String, Class<?>> map = (HashMap<String, Class<?>>) cache.get(OPTIONS_KEY_TYPE_MAP);
		if (Objects.isNull(map)) {
			map = new HashMap<>();
			for (Class<OptionEnum> clazz : ClassUtils.getAllClassByInterface(OptionEnum.class)) {
				for (OptionEnum oenum : clazz.getEnumConstants()) {
					map.put(oenum.key(), oenum.type());
				}
			}
			cache.set(OPTIONS_KEY_TYPE_MAP, map);
		}
		return map;
	}

}
