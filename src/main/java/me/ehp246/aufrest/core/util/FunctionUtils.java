package me.ehp246.aufrest.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Lei Yang
 *
 */
public class FunctionUtils {

	private FunctionUtils() {
		super();
	}

	public static boolean hasValue(final String value) {
		return value != null && !value.isBlank();
	}

	public static Stream<String> streamValues(final Collection<String> values) {
		return Optional.ofNullable(values).orElseGet(ArrayList::new).stream().filter(FunctionUtils::hasValue);
	}

	public static List<String> listValues(final Collection<String> values) {
		return streamValues(values).collect(Collectors.toList());
	}
}
