package me.ehp246.aufrest.api.rest;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Lei Yang
 *
 */
class HttpUtilsTest {

    @Test
    void encodePath_01() {
        Assertions.assertEquals("3%20%3D%201", HttpUtils.encodeUrlPath("3 = 1"));
        Assertions.assertEquals("3%20%26%3D%201", HttpUtils.encodeUrlPath("3 &= 1"));
        Assertions.assertEquals("3%20%26%3D%201%3A%20%2F%204%20%3F%205%3A",
                HttpUtils.encodeUrlPath("3 &= 1: / 4 ? 5:"));
        Assertions.assertEquals("%3F%2C%3D%2C%2F%2C%26%2C%3A", HttpUtils.encodeUrlPath("?,=,/,&,:"));
    }

    @Test
    void queryString_01() {
        Assertions.assertEquals("", HttpUtils.encodeQueryString(null));
    }

    @Test
    @Disabled
    void queryString_02() {
        final var queries = Map.of("email", List.of("test+1@email.com"));
        final var expected = UriComponentsBuilder.fromUriString("http://localhost")
                .queryParams(CollectionUtils.toMultiValueMap(queries)).toUriString();

        Assertions.assertEquals(expected, "http://localhost?" + HttpUtils.encodeQueryString(queries));
    }

    @Test
    @Disabled
    void queryString_03() {
        final var queries = Map.of("email", List.of("test=1@email&com"));

        final var expected = UriComponentsBuilder.fromUriString("http://localhost:123/")
                .queryParams(CollectionUtils.toMultiValueMap(queries)).toUriString();

        Assertions.assertEquals(expected, "http://localhost:123/?" + HttpUtils.encodeQueryString(queries));
    }

    @Test
    void expandPath_01() {
        Assertions.assertEquals("", HttpUtils.bindPlaceholder(null, null, HttpUtils::encodeUrlPath));
    }

    @Test
    void expandPath_02() {
        Assertions.assertEquals("get/1/path2/3", HttpUtils.bindPlaceholder("get/{path1}/path2/{path3}",
                Map.of("path1", "1", "path2", "2", "path3", "3"), HttpUtils::encodeUrlPath));
    }

    @Test
    void expandPath_03() {
        Assertions.assertEquals("get/1/path2/3%20%26%3D%201%3A%20%2F%204%20%3F%205%3A",
                HttpUtils.bindPlaceholder("get/{path1}/path2/{path3}",
                        Map.of("path1", "1", "path3", "3 &= 1: / 4 ? 5:"), HttpUtils::encodeUrlPath));
    }

    @Test
    void expandPath_04() {
        Assertions.assertEquals("get/1/path2/{path3}",
                HttpUtils.bindPlaceholder("get/{path1}/path2/{path3}", Map.of("path1", "1"), HttpUtils::encodeUrlPath));
    }
}
