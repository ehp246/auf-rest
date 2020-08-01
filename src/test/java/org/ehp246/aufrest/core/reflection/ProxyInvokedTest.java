package org.ehp246.aufrest.core.reflection;

import org.ehp246.aufrest.core.reflection.AnnotatedArgsCase001.AnnotatedArg;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Lei Yang
 *
 */
class ProxyInvokedTest {
	private final ReflectingType<AnnotatedArgsCase001> case001 = new ReflectingType<>(AnnotatedArgsCase001.class);

	@Test
	void argsAnnotated001() {
		final var invoked = new ProxyInvoked<>(null,
				case001.findMethod("m001", String.class, String.class, String.class), new String[] { "1", "2", "3" });

		final var map = invoked.mapAnnotatedArguments(AnnotatedArg.class, AnnotatedArg::value);

		Assertions.assertEquals(3, map.keySet().size());

		Assertions.assertEquals("1", map.get("arg1"));
		Assertions.assertEquals("2", map.get("arg2"));
		Assertions.assertEquals("3", map.get("arg3"));
	}

}
