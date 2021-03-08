package xyz.snwjas.blog.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * String Utils
 *
 * @author Myles Yang
 */
public class StrUtils {

	/**
	 * 移除字符串中的空白字符
	 */
	public static String removeBlank(String text) {
		if (StringUtils.isBlank(text)) {
			return "";
		}
		StringBuilder newString = new StringBuilder();
		char[] chars = text.toCharArray();
		for (char c : chars) {
			if (Character.isWhitespace(c)) {
				continue;
			}
			newString.append(c);
		}
		return newString.toString();
	}

}
