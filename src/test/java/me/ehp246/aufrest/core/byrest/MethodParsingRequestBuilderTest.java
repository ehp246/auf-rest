package me.ehp246.aufrest.core.byrest;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import me.ehp246.aufrest.api.rest.ByRestProxyConfig;
import me.ehp246.aufrest.api.spi.PropertyResolver;
import me.ehp246.test.TestUtil;

/**
 * @author Lei Yang
 *
 */
class MethodParsingRequestBuilderTest {
    private final PropertyResolver mockResolver = new MockEnvironment().withProperty("echo.base",
            "http://localhost")::resolveRequiredPlaceholders;
    private final ByRestProxyConfig proxyConfig = new ByRestProxyConfig("${echo.base}/", "timeout", "accept",
            "content-type");


    @Test
    void method_04() {
        final var captor = TestUtil.newCaptor(MethodTestCase001.class);

        captor.proxy().get();

        Assertions.assertEquals("GET",
                new MethodParsingRequestBuilder(captor.invocation().method(), proxyConfig, mockResolver)
                        .apply(captor.invocation().args()).method());
    }

    @Test
    void method_05() {
        final var captor = TestUtil.newCaptor(MethodTestCase001.class);

        captor.proxy().query();

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new MethodParsingRequestBuilder(captor.invocation().method(), proxyConfig, mockResolver)
                        .apply(captor.invocation().args()));
    }

    @Test
    void method_06() {
        final var captor = TestUtil.newCaptor(MethodTestCase001.class);

        captor.proxy().post();

        Assertions.assertEquals("POST",
                new MethodParsingRequestBuilder(captor.invocation().method(), proxyConfig, mockResolver)
                        .apply(captor.invocation().args()).method());
    }

    @Test
    void method_07() {
        final var captor = TestUtil.newCaptor(MethodTestCase001.class);

        captor.proxy().delete();

        Assertions.assertEquals("DELETE",
                new MethodParsingRequestBuilder(captor.invocation().method(), proxyConfig, mockResolver)
                        .apply(captor.invocation().args()).method());
    }

    @Test
    void method_08() {
        final var captor = TestUtil.newCaptor(MethodTestCase001.class);

        captor.proxy().put();

        final var request = new MethodParsingRequestBuilder(captor.invocation().method(), proxyConfig, mockResolver)
                .apply(captor.invocation().args());

        Assertions.assertEquals("PUT", request.method());
    }

    @Test
    void method_10() {
        final var captor = TestUtil.newCaptor(MethodTestCase001.class);

        captor.proxy().patch();

        Assertions.assertEquals("PATCH",
                new MethodParsingRequestBuilder(captor.invocation().method(), proxyConfig, mockResolver)
                        .apply(captor.invocation().args()).method());
    }

    @Test
    void method_11() {
        final var captor = TestUtil.newCaptor(MethodTestCase001.class);

        captor.proxy().create();

        Assertions.assertEquals("POST",
                new MethodParsingRequestBuilder(captor.invocation().method(), proxyConfig, mockResolver)
                        .apply(captor.invocation().args()).method());
    }

    @Test
    void method_12() {
        final var captor = TestUtil.newCaptor(MethodTestCase001.class);

        captor.proxy().remove();

        Assertions.assertEquals("DELETE",
                new MethodParsingRequestBuilder(captor.invocation().method(), proxyConfig, mockResolver)
                        .apply(captor.invocation().args()).method());
    }

    @Test
    void method_13() {
        final var captor = TestUtil.newCaptor(MethodTestCase001.class);

        captor.proxy().getBySomething();

        Assertions.assertEquals("GET",
                new MethodParsingRequestBuilder(captor.invocation().method(), proxyConfig, mockResolver)
                        .apply(captor.invocation().args()).method());
    }

    @Test
    void method_14() {
        final var captor = TestUtil.newCaptor(MethodTestCase001.class);

        captor.proxy().postByName();

        Assertions.assertEquals("POST",
                new MethodParsingRequestBuilder(captor.invocation().method(), proxyConfig, mockResolver)
                        .apply(captor.invocation().args()).method());
    }

    @Test
    void uri_01() {
        final var captor = TestUtil.newCaptor(UriTestCase001.class);

        captor.proxy().get();

        Assertions.assertEquals("http://localhost/get",
                new MethodParsingRequestBuilder(captor.invocation().method(), proxyConfig, mockResolver)
                        .apply(captor.invocation().args()).uri());
    }

    @Test
    void uri_03() {
        final var captor = TestUtil.newCaptor(UriTestCase001.class);

        captor.proxy().get("");

        Assertions.assertEquals("http://localhost/get1",
                new MethodParsingRequestBuilder(captor.invocation().method(), proxyConfig, mockResolver)
                        .apply(captor.invocation().args()).uri());
    }

    @Test
    void uri_04() {
        final var captor = TestUtil.newCaptor(UriTestCase001.class);

        captor.proxy().get(1);

        Assertions.assertEquals("http://localhost/",
                new MethodParsingRequestBuilder(captor.invocation().method(), proxyConfig, mockResolver)
                        .apply(captor.invocation().args()).uri());
    }

    @Test
    void path_01() {
        final var captor = TestUtil.newCaptor(PathTestCase001.class);

        captor.proxy().get("1", "3");

        Assertions.assertEquals("http://localhost/get/1/path2/3",
                new MethodParsingRequestBuilder(captor.invocation().method(), proxyConfig, mockResolver)
                        .apply(captor.invocation().args()).uri());
    }

    @Test
    void path_02() {
        final var captor = TestUtil.newCaptor(PathTestCase001.class);

        captor.proxy().get("4", "1", "3");

        /**
         * Method-level annotation overwrites type-level. This behavior is different
         * from Spring's RequestMapping.
         */
        Assertions.assertEquals("http://localhost/3/4",
                new MethodParsingRequestBuilder(captor.invocation().method(), proxyConfig, mockResolver)
                        .apply(captor.invocation().args()).uri());
    }

    @Test
    void path_03() {
        final var captor = TestUtil.newCaptor(PathTestCase001.class);

        captor.proxy().getByMap(Map.of("path1", "1", "path3", "3"));

        /**
         * Method-level annotation overwrites type-level. This behavior is different
         * from Spring's RequestMapping.
         */
        Assertions.assertEquals("http://localhost/get/1/path2/3",
                new MethodParsingRequestBuilder(captor.invocation().method(), proxyConfig, mockResolver)
                        .apply(captor.invocation().args()).uri());
    }

    @Test
    void path_04() {
        final var captor = TestUtil.newCaptor(PathTestCase001.class);

        captor.proxy().getByMap(Map.of("path1", "mapped1", "path3", "3"), "1");

        /**
         * Method-level annotation overwrites type-level. This behavior is different
         * from Spring's RequestMapping.
         */
        Assertions.assertEquals("http://localhost/get/1/path2/3",
                new MethodParsingRequestBuilder(captor.invocation().method(), proxyConfig, mockResolver)
                        .apply(captor.invocation().args()).uri());
    }

    @Test
    void path_05() {
        final var captor = TestUtil.newCaptor(PathTestCase001.class);

        captor.proxy().getByMap(Map.of("path1", "mapped1", "path3", "3 = 1"), "1");

        /**
         * Method-level annotation overwrites type-level. This behavior is different
         * from Spring's RequestMapping.
         */
        Assertions.assertEquals("http://localhost/get/1/path2/3%20%3D%201",
                new MethodParsingRequestBuilder(captor.invocation().method(), proxyConfig, mockResolver)
                        .apply(captor.invocation().args()).uri());
    }

    @Test
    void path_06() {
        final var captor = TestUtil.newCaptor(PathTestCase001.class);

        captor.proxy().get();

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new MethodParsingRequestBuilder(captor.invocation().method(), proxyConfig, mockResolver)
                        .apply(captor.invocation().args()).uri());
    }

    @Test
    void path_07() {
        final var captor = TestUtil.newCaptor(PathTestCase001.class);

        captor.proxy().get(null, null);

        /**
         * Not supporting optional variables.
         */
        Assertions.assertThrows(NullPointerException.class,
                () -> new MethodParsingRequestBuilder(captor.invocation().method(), proxyConfig, mockResolver)
                        .apply(captor.invocation().args()).uri());
    }

    @Test
    void path_08() {
        final var captor = TestUtil.newCaptor(PathTestCase001.class);

        captor.proxy().get("", "");

        Assertions.assertEquals("http://localhost/get//path2/",
                new MethodParsingRequestBuilder(captor.invocation().method(), proxyConfig, mockResolver)
                        .apply(captor.invocation().args()).uri());
    }
}
