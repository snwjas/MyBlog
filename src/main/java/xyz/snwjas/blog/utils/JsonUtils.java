package xyz.snwjas.blog.utils;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Map;

/**
 * Json utilities.
 */
@Component
@DependsOn("objectMapper")
public class JsonUtils {

	private static ObjectMapper objectMapper;

	@Autowired
	public JsonUtils(ObjectMapper objectMapper) {
		JsonUtils.objectMapper = objectMapper;
	}


	public static <T> T jsonToObject(@NonNull String json, @NonNull Class<T> type) throws IOException {
		Assert.hasText(json, "Json content must not be blank");
		Assert.notNull(type, "Target type must not be null");

		return objectMapper.readValue(json, type);
	}


	public static String objectToJson(@NonNull Object source) throws JsonProcessingException {
		Assert.notNull(source, "Source object must not be null");

		return objectMapper.writeValueAsString(source);
	}


	public static <T> T mapToObject(@NonNull Map<String, ?> sourceMap, @NonNull Class<T> type) throws IOException {
		Assert.notEmpty(sourceMap, "Source map must not be empty");

		// Serialize the map
		String json = objectToJson(sourceMap);

		// Deserialize the json format of the map
		return jsonToObject(json, type);
	}


	public static Map<?, ?> objectToMap(@NonNull Object source) throws IOException {

		// Serialize the source object
		String json = objectToJson(source);

		// Deserialize the json
		return jsonToObject(json, Map.class);
	}

}
