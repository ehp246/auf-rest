package me.ehp246.test.embedded.restfn;

import java.util.function.Supplier;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import me.ehp246.aufrest.api.rest.RestFn;
import me.ehp246.aufrest.api.rest.RestRequest;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
class RestFnTest {
    @Value("${local.server.port}")
    private int port;
    @Autowired
    private RestFn restFn;

    @Test
    void auth_01() {
        final var response = restFn.apply(new RestRequest() {

            @Override
            public String uri() {
                return "http://localhost:" + port + "/restfn/auth";
            }
        });

        Assertions.assertEquals(401, response.statusCode());
    }

    @Test
    void auth_02() {
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
