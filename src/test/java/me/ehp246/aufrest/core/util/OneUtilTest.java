package me.ehp246.aufrest.core.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Lei Yang
 *
 */
class OneUtilTest {

    @Test
    void test() {
        Assertions.assertEquals(false, OneUtil.hasValue(null));
        Assertions.assertEquals(false, OneUtil.hasValue(""));
        Assertions.assertEquals(false, OneUtil.hasValue("   "));
        Assertions.assertEquals(false, OneUtil.hasValue("\r\n"));
    }

}
