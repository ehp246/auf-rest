package me.ehp246.test.embedded.returns;

import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import me.ehp246.aufrest.api.configuration.AufRestConstants;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = AppConfig.class, webEnvironment = WebEnvironment.RANDOM_PORT, properties = {
        AufRestConstants.REST_LOGGER_ENABLED + "=true" })
class HeaderReturnTest {
    @Autowired
    private HeaderReturnCase testCase;

    @Test
    void header_01() {
        final var expected = UUID.randomUUID().toString();

        final var headers = testCase.get(expected);

        Assertions.assertEquals(expected, headers.map().get("x-aufrest-header").get(0));
    }

    @Test
    void headerNamed_02() {
        final var expected = UUID.randomUUID().toString();

        final var header = testCase.getNamed(expected);

        Assertions.assertEquals(expected, header);
    }

    @Test
    void headerMap_01() {
        final var expected = UUID.randomUUID().toString();

        final var headers = testCase.getMap(expected).get("x-aufrest-header");

        Assertions.assertEquals(1, headers.size());
        Assertions.assertEquals(expected, headers.get(0));
    }

    @Test
    void headerList_01() {
        final var expected = UUID.randomUUID().toString();

        final var headers = testCase.getList(expected);

        Assertions.assertEquals(1, headers.size());
        Assertions.assertEquals(expected, headers.get(0));
    }
}
