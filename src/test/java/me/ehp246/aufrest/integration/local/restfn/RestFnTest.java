package me.ehp246.aufrest.integration.local.restfn;

import java.util.function.Supplier;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;

import me.ehp246.aufrest.api.rest.HttpFn;
import me.ehp246.aufrest.api.rest.RestRequest;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
class RestFnTest {
    @LocalServerPort
    private int port;
    @Autowired
    private HttpFn restFn;

    @Test
    void auth_001() {
        final var response = restFn.apply(new RestRequest() {

            @Override
            public String uri() {
                return "http://localhost:" + port + "/restfn/auth";
            }
        });

        Assertions.assertEquals(401, response.statusCode());
    }

    @Test
    void auth_002() {
        final var response = restFn.apply(new RestRequest() {

            @Override
            public String uri() {
                return "http://localhost:" + port + "/restfn/auth";
            }

            @Override
            public Supplier<String> authSupplier() {
                return "Basic YmFzaWN1c2VyOnBhc3N3b3Jk"::toString;
            }

        });

        Assertions.assertEquals(200, response.statusCode());
    }

}
