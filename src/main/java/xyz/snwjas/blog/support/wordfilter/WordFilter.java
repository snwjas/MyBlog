package xyz.snwjas.blog.support.wordfilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 敏感词过滤器
 *
 * @author minghu.zhang
 * @gitee https://gitee.com/humingzhang/wordfilter
 */
@SuppressWarnings("rawtypes")
public class WordFilter {

	private final WordContext context;

	/**
	 * 敏感词表
	 */
	private final Map wordMap;

	/**
	 * 构造函数
	 */
	public WordFilter(WordContext context) {
		this.context = context;
		this.wordMap = context.getWordMap();
	}

	public WordContext getContext() {
		return context;
	}

	/**
	 * 替换敏感词
	 *
	 * @param text 输入文本
	 */
	public String replace(final String text) {
		return replace(text, 0, '*');
	}

	/**
	 * 替换敏感词
	 *
	 * @param text   输入文本
	 * @param symbol 替换符号
	 */
	public String replace(final String text, final char symbol) {
		return replace(text, 0, symbol);
	}

	/**
	 * 替换敏感词
	 *
	 * @param text   输入文本
	 * @param skip   文本距离
	 * @param symbol 替换符号
	 */
	public String replace(final String text, final int skip, final char symbol) {
		char[] charset = text.toCharArray();
		for (int i = 0; i < charset.length; i++) {
			FlagIndex fi = getFlagIndex(charset, i, skip);
			if (fi.isFlag()) {
				if (!fi.isWhiteWord()) {
					for (int j : fi.getIndex()) {
						charset[j] = symbol;
					}
				} else {
					i += fi.getIndex().size() - 1;
				}
			}
		}
		return new String(charset);
	}

	/**
	 * 是否包含敏感词
	 *
	 * @param text 输入文本
	 */
	public boolean include(final String text) {
		return include(text, 0);
	}

	/**
	 * 是否包含敏感词
	 *
	 * @param text 输入文本
	 * @param skip 文本距离
	 */
	public boolean include(final String text, final int skip) {
		boolean include = false;
		char[] charset = text.toCharArray();
		for (int i = 0; i < charset.length; i++) {
			FlagIndex fi = getFlagIndex(charset, i, skip);
			if (fi.isFlag()) {
				if (fi.isWhiteWord()) {
					i += fi.getIndex().size() - 1;
				} else {
					include = true;
					break;
				}
			}
		}
		return include;
	}

	/**
	 * 获取敏感词数量
	 *
	 * @param text 输入文本
	 */
	public int wordCount(final String text) {
		return wordCount(text, 0);
	}

	/**
	 * 获取敏感词数量
	 *
	 * @param text 输入文本
	 * @param skip 文本距离
	 */
	public int wordCount(final String text, final int skip) {
		int count = 0;
		char[] charset = text.toCharArray();
		for (int i = 0; i < charset.length; i++) {
			FlagIndex fi = getFlagIndex(charset, i, skip);
			if (fi.isFlag()) {
				if (fi.isWhiteWord()) {
					i += fi.getIndex().size() - 1;
				} else {
					count++;
				}
			}
		}
		return count;
	}

	/**
	 * 获取敏感词列表
	 *
	 * @param text 输入文本
	 */
	public List<String> wordList(final String text) {
		return wordList(text, 0);
	}

	/**
	 * 获取敏感词列表
	 *
	 * @param text 输入文本
	 * @param skip 文本距离
	 */
	public List<String> wordList(final String text, final int skip) {
		List<String> wordList = new ArrayList<>();
		char[] charset = text.toCharArray();
		for (int i = 0; i < charset.length; i++) {
			FlagIndex fi = getFlagIndex(charset, i, skip);
			if (fi.isFlag()) {
				if (fi.isWhiteWord()) {
					i += fi.getIndex().size() - 1;
				} else {
					StringBuilder builder = new StringBuilder();
					for (int j : fi.getIndex()) {
						char word = text.charAt(j);
						builder.append(word);
					}
					wordList.add(builder.toString());
				}
			}
		}
		return wordList;
	}

	/**
	 * 获取标记索引
	 *
	 * @param charset 输入文本
	 * @param begin   检测起始
	 * @param skip    文本距离
	 */
	private FlagIndex getFlagIndex(final char[] charset, final int begin, final int skip) {
		FlagIndex fi = new FlagIndex();

		Map current = wordMap;
		boolean flag = false;
		int count = 0;
		List<Integer> index = new ArrayList<>();
		for (int i = begin; i < charset.length; i++) {
			char word = charset[i];
			Map mapTree = (Map) current.get(word);
			if (count > skip || (i == begin && Objects.isNull(mapTree))) {
				break;
			}
			if (Objects.nonNull(mapTree)) {
				current = mapTree;
				count = 0;
				index.add(i);
			} else {
				count++;
				if (flag && count > skip) {
					break;
				}
			}
			if ("1".equals(current.get("isEnd"))) {
				flag = true;
			}
			if ("1".equals(current.get("isWhiteWord"))) {
				fi.setWhiteWord(true);
				break;
			}
		}

		fi.setFlag(flag);
		fi.setIndex(index);

		return fi;
	}
}
