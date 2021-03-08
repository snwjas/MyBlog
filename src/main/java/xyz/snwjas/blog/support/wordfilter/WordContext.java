package xyz.snwjas.blog.support.wordfilter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 词库上下文环境
 * <p>
 * 初始化敏感词库，将敏感词加入到HashMap中，构建DFA算法模型
 *
 * @author minghu.zhang
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class WordContext {

	/**
	 * 敏感词字典
	 */
	private final Map wordMap = new HashMap(1024);

	/**
	 * 是否已初始化
	 */
	private boolean init;


	public WordContext(Set<String> blackList, Set<String> whiteList) {

	}

	public WordContext(String blackList, String whiteList) {
		initKeyWord(blackList, whiteList);
	}

	/**
	 * 获取初始化的敏感词列表
	 *
	 * @return 敏感词列表
	 */
	public Map getWordMap() {
		return wordMap;
	}

	/**
	 * 清除敏感词列表
	 */
	public void clearWord() {
		wordMap.clear();
	}

	/**
	 * 初始化
	 *
	 * @param blackList 黑名单文件路径
	 * @param whiteList 白名单文件路径
	 */
	private synchronized void initKeyWord(String blackList, String whiteList) {
		try {
			if (!init) {
				// 将敏感词库加入到HashMap中
				addWord(readWordFile(blackList), WordType.BLACK);
				// 将非敏感词库也加入到HashMap中
				addWord(readWordFile(whiteList), WordType.WHITE);
			}
			init = true;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 初始化
	 *
	 * @param blackList 黑名单文件路径
	 * @param whiteList 白名单文件路径
	 */
	private synchronized void initKeyWord(Set<String> blackList, Set<String> whiteList) {
		try {
			if (!init) {
				// 将敏感词库加入到HashMap中
				addWord(blackList, WordType.BLACK);
				// 将非敏感词库也加入到HashMap中
				addWord(whiteList, WordType.WHITE);
			}
			init = true;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 读取敏感词库，将敏感词放入HashSet中，构建一个DFA算法模型：<br>
	 * 中 = { isEnd = 0 国 = {<br>
	 * isEnd = 1 人 = {isEnd = 0 民 = {isEnd = 1} } 男 = { isEnd = 0 人 = { isEnd = 1 }
	 * } } } 五 = { isEnd = 0 星 = { isEnd = 0 红 = { isEnd = 0 旗 = { isEnd = 1 } } } }
	 */
	public void addWord(Iterable<String> wordList, WordType wordType) {
		Map nowMap;
		Map<String, String> newWorMap;
		// 迭代keyWordSet
		for (String key : wordList) {
			nowMap = wordMap;
			for (int i = 0; i < key.length(); i++) {
				// 转换成char型
				char keyChar = key.charAt(i);
				// 获取
				Object wordMap = nowMap.get(keyChar);
				// 如果存在该key，直接赋值
				if (wordMap != null) {
					nowMap = (Map) wordMap;
				} else {
					// 不存在则构建一个map，同时将isEnd设置为0，因为他不是最后一个
					newWorMap = new HashMap<>(4);
					// 不是最后一个
					newWorMap.put("isEnd", String.valueOf(EndType.HAS_NEXT.ordinal()));
					nowMap.put(keyChar, newWorMap);
					nowMap = newWorMap;
				}

				if (i == key.length() - 1) {
					// 最后一个
					nowMap.put("isEnd", String.valueOf(EndType.IS_END.ordinal()));
					nowMap.put("isWhiteWord", String.valueOf(wordType.ordinal()));
				}
			}
		}
	}

	/**
	 * 在线删除敏感词
	 *
	 * @param wordList 敏感词列表
	 * @param wordType 黑名单 BLACk，白名单WHITE
	 */
	public void removeWord(Iterable<String> wordList, WordType wordType) {
		Map nowMap;
		for (String key : wordList) {
			List<Map> cacheList = new ArrayList<>();
			nowMap = wordMap;
			for (int i = 0; i < key.length(); i++) {
				char keyChar = key.charAt(i);

				Object map = nowMap.get(keyChar);
				if (map != null) {
					nowMap = (Map) map;
					cacheList.add(nowMap);
				} else {
					throw new RuntimeException("操作失败，黑白名单都没有该词汇[" + key + "]");
				}

				if (i == key.length() - 1) {
					char[] keys = key.toCharArray();
					boolean cleanable = false;
					char lastChar = 0;
					for (int j = cacheList.size() - 1; j >= 0; j--) {
						Map cacheMap = cacheList.get(j);
						if (j == cacheList.size() - 1) {
							if (String.valueOf(WordType.BLACK.ordinal()).equals(cacheMap.get("isWhiteWord"))) {
								if (wordType == WordType.WHITE) {
									throw new RuntimeException("操作失败，白名单没有该词汇[" + key + "]");
								}
							}
							if (String.valueOf(WordType.WHITE.ordinal()).equals(cacheMap.get("isWhiteWord"))) {
								if (wordType == WordType.BLACK) {
									throw new RuntimeException("操作失败，黑名单没有该词汇[" + key + "]");
								}
							}
							cacheMap.remove("isWhiteWord");
							cacheMap.remove("isEnd");
							if (cacheMap.size() == 0) {
								cleanable = true;
								continue;
							}
						}
						if (cleanable) {
							Object isEnd = cacheMap.get("isEnd");
							if (String.valueOf(EndType.IS_END.ordinal()).equals(isEnd)) {
								cleanable = false;
							}
							cacheMap.remove(lastChar);
						}
						lastChar = keys[j];
					}

					if (cleanable) {
						wordMap.remove(lastChar);
					}
				}
			}
		}
	}

	/**
	 * 读取敏感词库中的内容，将内容添加到set集合中
	 */
	private Set<String> readWordFile(String file) throws Exception {
		Set<String> set;
		// 字符编码
		String encoding = StandardCharsets.UTF_8.name();
		try (BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(this.getClass().getResourceAsStream(file), encoding)
		)) {
			set = new HashSet<>();
			String txt;
			// 读取文件，将文件内容放入到set中
			while ((txt = bufferedReader.readLine()) != null) {
				set.add(txt);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return set;
	}
}
