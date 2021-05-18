package me.ehp246.aufrest.integration.local.restfn;

import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.UUID;
import java.util.function.Supplier;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;

import me.ehp246.aufrest.api.rest.RestFn;
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
    private RestFn restFn;

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

    @Test
    void bodyhandler_001() {
        final var response = restFn.apply(new RestRequest() {

            @Override
            public String uri() {
                return "http://localhost:" + port + "/restfn/path/1";
            }

            @Override
            public BodyHandler<?> bodyHandler() {
                return BodyHandlers.discarding();
            }
        });

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(null, response.body());
    }

    @Test
    void bodyhandler_002() {
        final var id = UUID.randomUUID().toString();
        final var response = restFn.apply(new RestRequest() {

            @Override
            public String uri() {
                return "http://localhost:" + port + "/restfn/path/" + id;
            }

            @Override
            public BodyHandler<?> bodyHandler() {
                return BodyHandlers.ofString();
            }
        });

        Assertions.assertEquals(id, response.body());
    }
}
