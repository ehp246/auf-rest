package me.ehp246.aufrest.api.rest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Lei Yang
 *
 */
class HttpUtilsTest {

    @Test
    void test() {
        Assertions.assertEquals("3%20%3D%201", HttpUtils.encodUrlPath("3 = 1"));
        Assertions.assertEquals("3%20%26%3D%201", HttpUtils.encodUrlPath("3 &= 1"));
        Assertions.assertEquals("3%20%26%3D%201%3A%20%2F%204%20%3F%205%3A", HttpUtils.encodUrlPath("3 &= 1: / 4 ? 5:"));
        Assertions.assertEquals("%3F%2C%3D%2C%2F%2C%26%2C%3A", HttpUtils.encodUrlPath("?,=,/,&,:"));
    }

}
