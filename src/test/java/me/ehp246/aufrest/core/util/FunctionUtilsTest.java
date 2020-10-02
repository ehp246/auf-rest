package me.ehp246.aufrest.core.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Lei Yang
 *
 */
class FunctionUtilsTest {

	@Test
	void test() {
		Assertions.assertEquals(false, FunctionUtils.hasValue(null));
		Assertions.assertEquals(false, FunctionUtils.hasValue(""));
		Assertions.assertEquals(false, FunctionUtils.hasValue("   "));
		Assertions.assertEquals(false, FunctionUtils.hasValue("\r\n"));
	}

}
