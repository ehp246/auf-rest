package me.ehp246.aufrest.core.rest.uri;

import java.net.http.HttpResponse;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.env.MockEnvironment;

import me.ehp246.aufrest.api.rest.ClientConfig;
import me.ehp246.aufrest.api.spi.PropertyResolver;
import me.ehp246.aufrest.core.rest.ByRestProxyFactory;
import me.ehp246.aufrest.core.rest.DefaultProxyMethodParser;
import me.ehp246.aufrest.core.rest.uri.TestCase.PathObject;
import me.ehp246.test.mock.MockRestFnProvider;

/**
 * @author Lei Yang
 *
 */
class ByRestProxyFactoryUriTest {
    private final MockRestFnProvider restFnProvider = new MockRestFnProvider(Mockito.mock(HttpResponse.class));

    private final PropertyResolver env = new MockEnvironment().withProperty("echo.base",
            "http://localhost")::resolveRequiredPlaceholders;

    private final ByRestProxyFactory factory = new ByRestProxyFactory(restFnProvider, new ClientConfig(),
            new DefaultProxyMethodParser(env, name -> null, name -> r -> null, binding -> r -> null));

    private final TestCase testCase = factory.newInstance(TestCase.class);

    @BeforeEach
    void beforeEach() {
        restFnProvider.takeReq();
    }

    @Test
    void path001() {
        testCase.getByPathVariable("1", "3");

        final var request = restFnProvider.getReq();

        Assertions.assertEquals(true, request.uri() == request.uri());
        Assertions.assertEquals("http://localhost/get/1/path2/3", request.uri());
    }

    @Test
    void path002() {
        testCase.getByPathParam("4", "1", "3");

        final var request = restFnProvider.getReq();

        /**
         * Method-level annotation overwrites type-level. This behavior is different
         * from Spring's RequestMapping.
         */
        Assertions.assertEquals("http://localhost/3/4", request.uri(), "Should overwrite type-level annotation");
    }

    @Test
    void path003() {
        testCase.getByPathVariable("1", "3");

        final var request = restFnProvider.getReq();

        /**
         * Method-level annotation overwrites type-level. This behavior is different
         * from Spring's RequestMapping.
         */
        Assertions.assertEquals("http://localhost/get/1/path2/3", request.uri(),
                "Should overwrite type-level annotation");
    }

    @Test
    void uri_004() {
        testCase.getWithPlaceholder();

        Assertions.assertEquals("http://localhost/get", restFnProvider.getReq().uri());
    }

    @Test
    void uri_005() {
        testCase.get001();

        Assertions.assertEquals("http://localhost/", restFnProvider.getReq().uri());
    }

    @Test
    void pathMap001() {
        testCase.getByMap(Map.of("path1", "1", "path3", "3"));

        final var request = restFnProvider.getReq();

        /**
         * Method-level annotation overwrites type-level. This behavior is different
         * from Spring's RequestMapping.
         */
        Assertions.assertEquals("http://localhost/get/1/path2/3", request.uri());
    }

    @Test
    void pathMap002() {
        testCase.getByMap(Map.of("path1", "mapped1", "path3", "3"), "1");

        final var request = restFnProvider.getReq();

        /**
         * Explicit parameter takes precedence.
         */
        Assertions.assertEquals("http://localhost/get/1/path2/3", request.uri());
    }

    @Test
    void pathMap_02() {
        testCase.getByMap(Map.of("path1", "mapped1", "path3", "3 &= 1: / 4 ? 5:"), "1");

        final var request = restFnProvider.getReq();

        /**
         * Explicit parameter takes precedence.
         */
        Assertions.assertEquals("http://localhost/get/1/path2/3%20%26%3D%201%3A%20%2F%204%20%3F%205%3A", request.uri());
    }

    /*
     * TODO
     */
    // @Test
    void pathObject001() {
        testCase.getByObject(new PathObject() {

            @Override
            public String getPath3() {
                return "3";
            }

            @Override
            public String getPath1() {
                return "1";
            }
        });

        final var request = restFnProvider.getReq();

        Assertions.assertEquals("http://localhost/get/1/path2/3", request.uri());
    }
}
