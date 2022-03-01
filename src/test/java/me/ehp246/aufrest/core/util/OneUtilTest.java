package me.ehp246.aufrest.core.util;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Lei Yang
 *
 */
class OneUtilTest {
    @Test
    void form_01() {
        final var encoded = OneUtil.formUrlEncodedBody(Map.of("name", List.of(UUID.randomUUID().toString())));

        Assertions.assertEquals(2, encoded.split("=").length);
    }

    @Test
    void form_02() {
        final var encoded = OneUtil.formUrlEncodedBody(
                Map.of("name", List.of(UUID.randomUUID().toString(), UUID.randomUUID().toString())));

        Assertions.assertEquals(3, encoded.split("=").length);
    }

    @Test
    void form_03() {
        final var encoded = OneUtil.formUrlEncodedBody(Map.of("name", List.of("")));

        Assertions.assertEquals(1, encoded.split("=").length);
    }

    @Test
    void form_04() {
        final var encoded = OneUtil
                .formUrlEncodedBody(Map.of("name", List.of(""), "id", List.of(UUID.randomUUID().toString())));

        Assertions.assertEquals(2, encoded.split("=").length);
        Assertions.assertEquals(2, encoded.split("&").length);
    }

    @Test
    void form_05() {
        final var encoded = OneUtil.formUrlEncodedBody(Map.of("name", List.of("="), "id", List.of("&")));

        Assertions.assertEquals(3, encoded.split("=").length);
        Assertions.assertEquals(2, encoded.split("&").length);
    }
}
