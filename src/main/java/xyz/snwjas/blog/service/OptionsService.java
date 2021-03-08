package xyz.snwjas.blog.service;

import xyz.snwjas.blog.model.entity.OptionsEntity;
import xyz.snwjas.blog.model.vo.OptionVO;

import java.util.Map;

/**
 * Options Service
 *
 * @author Myles Yang
 */
public interface OptionsService {

	/**
	 * 获取博客设置值
	 *
	 * @param optionKey key
	 * @return {@link OptionVO} if key exists, else null
	 */
	OptionVO get(String optionKey);

	/**
	 * 设置值，如果存在改变，不存在新建
	 */
	int setAnyway(String key, String value);

	/**
	 * 设置值，如果键不存在
	 */
	int setIfAbsence(String key, String value);

	/**
	 * 设置值，如果键存在
	 */
	int setIfPresent(String key, String value);

	/**
	 * 获取所有键值
	 */
	Map<String, Object> listAll();

	/**
	 * 获取必要的键值
	 */
	Map<String, Object> listNecessary();

	/**
	 * 清空查询出来的 options Map缓存
	 */
	boolean resetOptionsCache();

	/**
	 * OptionsEntity to OptionVO
	 */
	OptionVO covertToVO(OptionsEntity optionsEntity);

}
