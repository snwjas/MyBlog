package xyz.snwjas.blog.model.enums;

import org.springframework.util.Assert;

import java.util.stream.Stream;

/**
 * Value Enum
 */
public interface ValueEnum<V> {

	static <V, E extends ValueEnum<V>> E valueToEnum(Class<E> enumType, V value) {
		Assert.notNull(enumType, "enum type must not be null");
		Assert.isTrue(enumType.isEnum(), "type must be an enum type");
		Assert.notNull(value, "value must not be null");

		return Stream.of(enumType.getEnumConstants())
				.filter(item -> item.getValue().equals(value))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("unknown value: " + value));
	}

	V getValue();
}
