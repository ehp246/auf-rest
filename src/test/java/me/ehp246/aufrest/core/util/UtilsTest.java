package me.ehp246.aufrest.core.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Lei Yang
 *
 */
class UtilsTest {

	@Test
	void test() {
		Assertions.assertEquals(false, Utils.hasValue(null));
		Assertions.assertEquals(false, Utils.hasValue(""));
		Assertions.assertEquals(false, Utils.hasValue("   "));
		Assertions.assertEquals(false, Utils.hasValue("\r\n"));
	}

}
