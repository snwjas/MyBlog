package xyz.snwjas.blog.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * URL utils
 *
 * @author Myles Yang
 */
public class URLUtils {

	public static final String DEFAULT_ENCODING = StandardCharsets.UTF_8.name();

	// URL 中合法字符
	public static final Pattern PATTERN = Pattern.compile("[^0-9a-zA-Z-_.~!*'();:@&=+$,/?#\\[\\]]");

	/**
	 * 编码整个链接
	 */
	public static String encodeAll(String url) {
		Matcher m = PATTERN.matcher(url);
		StringBuffer b = new StringBuffer();
		try {
			while (m.find()) {
				m.appendReplacement(b, URLEncoder.encode(m.group(0), DEFAULT_ENCODING));
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}
		m.appendTail(b);
		return b.toString();
	}

	/**
	 * 编码链接的最后部分
	 * 如：localhost:9527//index/img-百度logo.jpg，只编码 img-百度logo.jpg
	 */
	public static String encodeLast(String url) {
		int i = url.lastIndexOf('/');
		String last = url.substring(i + 1);
		String encodedLast;
		try {
			encodedLast = URLEncoder.encode(last, DEFAULT_ENCODING);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}
		return url.substring(0, i) + encodedLast;
	}

	public static String decode(String str) {
		try {
			return URLDecoder.decode(str, DEFAULT_ENCODING);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

}
