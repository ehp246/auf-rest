package me.ehp246.aufrest.core.byrest.uri;

import java.net.http.HttpResponse;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.env.MockEnvironment;

import me.ehp246.aufrest.api.rest.ClientConfig;
import me.ehp246.aufrest.api.rest.RestFn;
import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.api.spi.PropertyResolver;
import me.ehp246.aufrest.core.byrest.ByRestProxyFactory;
import me.ehp246.aufrest.core.byrest.DefaultProxyMethodParser;
import me.ehp246.aufrest.core.byrest.uri.TestCase001.PathObject;

/**
 * @author Lei Yang
 *
 */
class UriTest {
    private final AtomicReference<RestRequest> reqRef = new AtomicReference<>();

    private final RestFn restFn = (req, con) -> {
        reqRef.set(req);
        return Mockito.mock(HttpResponse.class);
    };
    private final PropertyResolver env = new MockEnvironment().withProperty("echo.base",
            "http://localhost")::resolveRequiredPlaceholders;

    private final ByRestProxyFactory factory = new ByRestProxyFactory(cfg -> restFn, new ClientConfig(),
            new DefaultProxyMethodParser(env, name -> null, name -> r -> null, binding -> r -> null));

    private final TestCase001 case001 = factory.newInstance(TestCase001.class);

    @BeforeEach
    void beforeEach() {
        reqRef.set(null);
    }

    @Test
    void path001() {
        case001.getByPathVariable("1", "3");

        final var request = reqRef.get();

        Assertions.assertEquals(true, request.uri() == request.uri());
        Assertions.assertEquals("http://localhost/get/1/path2/3", request.uri());
    }

    @Test
    void path002() {
        case001.getByPathParam("4", "1", "3");

        final var request = reqRef.get();

        /**
         * Method-level annotation overwrites type-level. This behavior is different
         * from Spring's RequestMapping.
         */
        Assertions.assertEquals("http://localhost/3/4", request.uri(), "Should overwrite type-level annotation");
    }

    @Test
    void path003() {
        case001.getByPathVariable("1", "3");

        final var request = reqRef.get();

        /**
         * Method-level annotation overwrites type-level. This behavior is different
         * from Spring's RequestMapping.
         */
        Assertions.assertEquals("http://localhost/get/1/path2/3", request.uri(),
                "Should overwrite type-level annotation");
    }

    @Test
    void uri_004() {
        case001.getWithPlaceholder();

        Assertions.assertEquals("http://localhost/get", reqRef.get().uri());
    }

    @Test
    void uri_005() {
        case001.get001();

        Assertions.assertEquals("http://localhost/", reqRef.get().uri());
    }

    @Test
    void pathMap001() {
        case001.getByMap(Map.of("path1", "1", "path3", "3"));

        final var request = reqRef.get();

        /**
         * Method-level annotation overwrites type-level. This behavior is different
         * from Spring's RequestMapping.
         */
        Assertions.assertEquals("http://localhost/get/1/path2/3", request.uri());
    }

    @Test
    void pathMap002() {
        case001.getByMap(Map.of("path1", "mapped1", "path3", "3"), "1");

        final var request = reqRef.get();

        /**
         * Explicit parameter takes precedence.
         */
        Assertions.assertEquals("http://localhost/get/1/path2/3", request.uri());
    }

    @Test
    void pathMap_02() {
        case001.getByMap(Map.of("path1", "mapped1", "path3", "3 = 1"), "1");

        final var request = reqRef.get();

        /**
         * Explicit parameter takes precedence.
         */
        Assertions.assertEquals("http://localhost/get/1/path2/3%20%3D%201", request.uri());
    }

    /*
     * TODO
     */
    // @Test
    void pathObject001() {
        case001.getByObject(new PathObject() {

            @Override
            public String getPath3() {
                return "3";
            }

            @Override
            public String getPath1() {
                return "1";
            }
        });

        final var request = reqRef.get();

        Assertions.assertEquals("http://localhost/get/1/path2/3", request.uri());
    }
}
