package me.ehp246.test.local.restfn;

import java.net.http.HttpResponse.BodyHandlers;
import java.util.function.Supplier;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import me.ehp246.aufrest.api.rest.RestFn;
import me.ehp246.aufrest.api.rest.RestFn.ResponseConsumer;
import me.ehp246.aufrest.api.rest.RestRequest;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
class RestFnTest {
    private final static ResponseConsumer consumer = BodyHandlers::discarding;

    @Value("${local.server.port}")
    private int port;
    @Autowired
    private RestFn restFn;

    @Test
    void auth_001() {
        final var response = restFn.apply(new RestRequest() {

            @Override
            public String uri() {
                return "http://localhost:" + port + "/restfn/auth";
            }
        }, consumer);

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

        }, consumer);

        Assertions.assertEquals(200, response.statusCode());
    }

}
