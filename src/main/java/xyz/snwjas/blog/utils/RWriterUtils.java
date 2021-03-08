package xyz.snwjas.blog.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import xyz.snwjas.blog.model.R;

import javax.servlet.ServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 返回 响应 结果 工具
 *
 * @author Myles Yang
 */
public class RWriterUtils {

	/**
	 * 默认编码
	 */
	private static final String DEFAULT_ENCODING = StandardCharsets.UTF_8.name();


	public static void writeTextHtml(ServletResponse response, String textHtml) {
		writeString(response, "text/html", DEFAULT_ENCODING, textHtml);
	}

	public static void writeJson(ServletResponse response, String jsonString) {
		writeString(response, "application/json", DEFAULT_ENCODING, jsonString);
	}

	public static void writeJson(ServletResponse response, R r) {
		try {
			writeJson(response, JsonUtils.objectToJson(r));
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	public static void writeString(ServletResponse response, String contentType, String encoding, String string) {
		try {
			response.setContentType(contentType + ";charset=" + encoding);
			response.getWriter().write(string);
			response.getWriter().close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


}
