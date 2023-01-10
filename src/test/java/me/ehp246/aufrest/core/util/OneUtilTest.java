package me.ehp246.aufrest.core.util;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import me.ehp246.aufrest.api.rest.HttpUtils;

/**
 * @author Lei Yang
 *
 */
class OneUtilTest {
    @Test
    void form_01() {
        final var encoded = HttpUtils.encodeFormUrlBody(Map.of("name", List.of(UUID.randomUUID().toString())));

        Assertions.assertEquals(2, encoded.split("=").length);
    }

    @Test
    void form_02() {
        final var encoded = HttpUtils.encodeFormUrlBody(
                Map.of("name", List.of(UUID.randomUUID().toString(), UUID.randomUUID().toString())));

        Assertions.assertEquals(3, encoded.split("=").length);
    }

    @Test
    void form_03() {
        final var encoded = HttpUtils.encodeFormUrlBody(Map.of("name", List.of("")));

        Assertions.assertEquals(1, encoded.split("=").length);
    }

    @Test
    void form_04() {
        final var encoded = HttpUtils
                .encodeFormUrlBody(Map.of("name", List.of(""), "id", List.of(UUID.randomUUID().toString())));

        Assertions.assertEquals(2, encoded.split("=").length);
        Assertions.assertEquals(2, encoded.split("&").length);
    }

    @Test
    void form_05() {
        final var encoded = HttpUtils.encodeFormUrlBody(Map.of("name", List.of("="), "id", List.of("&")));

        Assertions.assertEquals(3, encoded.split("=").length);
        Assertions.assertEquals(2, encoded.split("&").length);
    }

    @Test
    void queryParamMap_01() {
        final var mapped = OneUtil.toQueryParamMap(Map.of("k1", List.of("v1"), "k2", List.of("v2")));

        Assertions.assertEquals(2, mapped.size());

        Assertions.assertEquals(1, mapped.get("k1").size());
        Assertions.assertEquals("v1", mapped.get("k1").get(0));

        Assertions.assertEquals(1, mapped.get("k2").size());
        Assertions.assertEquals("v2", mapped.get("k2").get(0));
    }

    @Test
    void queryParamMap_02() {
        final var mapped = OneUtil.toQueryParamMap(Map.of("k1", List.of("v1", ""), "k2", List.of("v2")));

        Assertions.assertEquals(2, mapped.size());

        Assertions.assertEquals(2, mapped.get("k1").size());
        Assertions.assertEquals("v1", mapped.get("k1").get(0));
        Assertions.assertEquals("", mapped.get("k1").get(1));

        Assertions.assertEquals(1, mapped.get("k2").size());
        Assertions.assertEquals("v2", mapped.get("k2").get(0));
    }

    @Test
    void queryParamMap_03() {
        final var mapped = OneUtil
                .toQueryParamMap(Map.of("k1", List.of("v1", ""), "k2", List.of("v2"), "k3", List.of("")));

        Assertions.assertEquals(3, mapped.size());

        Assertions.assertEquals(2, mapped.get("k1").size());
        Assertions.assertEquals("v1", mapped.get("k1").get(0));
        Assertions.assertEquals("", mapped.get("k1").get(1));

        Assertions.assertEquals(1, mapped.get("k2").size());
        Assertions.assertEquals("v2", mapped.get("k2").get(0));

        Assertions.assertEquals(1, mapped.get("k3").size());
        Assertions.assertEquals("", mapped.get("k3").get(0));

    }

    @Test
    void queryParamMap_04() {
        final var l1 = new ArrayList<Object>();
        l1.add("v1");
        l1.add(null);

        final var now = Instant.now();
        final var mapped = OneUtil.toQueryParamMap(Map.of("k1", l1, "k2", List.of(now)));

        Assertions.assertEquals(2, mapped.size());

        Assertions.assertEquals(2, mapped.get("k1").size());
        Assertions.assertEquals("v1", mapped.get("k1").get(0));
        Assertions.assertEquals(null, mapped.get("k1").get(1));

        Assertions.assertEquals(1, mapped.get("k2").size());
        Assertions.assertEquals(now.toString(), mapped.get("k2").get(0));
    }

    @Test
    void queryParamMap_05() {
        final var m = new HashMap<String, List<Object>>();
        m.put("k1", null);
        m.put("k2", null);

        final var mapped = OneUtil.toQueryParamMap(m);

        Assertions.assertEquals(2, mapped.size());

        Assertions.assertEquals(0, mapped.get("k1").size());

        Assertions.assertEquals(0, mapped.get("k2").size(), "should have the key but no value");
    }

    @Test
    void queryParamMap_06() {
        final var mapped = OneUtil.toQueryParamMap(Map.of());

        Assertions.assertEquals(0, mapped.size());
    }

    @Test
    void queryParamMap_07() {
        final var mapped = OneUtil.toQueryParamMap(null);

        Assertions.assertEquals(0, mapped.size());
    }

    @Test
    void queryParamMap_08() {
        final var mapped = OneUtil.toQueryParamMap(Map.of("k1", List.of(List.of("v1", "v2")), "k2", List.of("v3")));

        Assertions.assertEquals(2, mapped.size());

        Assertions.assertEquals(2, mapped.get("k1").size());
        Assertions.assertEquals("v1", mapped.get("k1").get(0));
        Assertions.assertEquals("v2", mapped.get("k1").get(1));

        Assertions.assertEquals(1, mapped.get("k2").size());
        Assertions.assertEquals("v3", mapped.get("k2").get(0));
    }

    @Test
    void queryParamMap_09() {
        final var l = new ArrayList<Object>();
        l.add("v3");
        l.add(null);

        final var mapped = OneUtil.toQueryParamMap(Map.of("k1", List.of(List.of("v1", "v2")), "k2", List.of(l)));

        Assertions.assertEquals(2, mapped.size());

        Assertions.assertEquals(2, mapped.get("k1").size());
        Assertions.assertEquals("v1", mapped.get("k1").get(0));
        Assertions.assertEquals("v2", mapped.get("k1").get(1));

        Assertions.assertEquals(2, mapped.get("k2").size());
        Assertions.assertEquals("v3", mapped.get("k2").get(0));
        Assertions.assertEquals(null, mapped.get("k2").get(1));
    }

    @Test
    void queryParamMap_10() {
        final var mapped = OneUtil
                .toQueryParamMap(Map.of("", List.of(Map.of("k1", "v2"), Map.of("k1", "v2")), "k2", List.of("v3")));

        Assertions.assertEquals(2, mapped.size());

        Assertions.assertEquals(2, mapped.get("k1").size());
        Assertions.assertEquals("v2", mapped.get("k1").get(0));
        Assertions.assertEquals("v2", mapped.get("k1").get(1));

        Assertions.assertEquals(1, mapped.get("k2").size());
        Assertions.assertEquals("v3", mapped.get("k2").get(0));
    }
}
