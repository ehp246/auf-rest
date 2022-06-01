package me.ehp246.aufrest.core.byrest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import me.ehp246.aufrest.api.rest.ByRestProxyConfig;
import me.ehp246.aufrest.api.spi.PropertyResolver;
import me.ehp246.test.TestUtil;

/**
 * @author Lei Yang
 *
 */
class MethodParsingRequestBuilderTest {
    private final PropertyResolver resolver = Object::toString;
    private final ByRestProxyConfig proxyConfig = new ByRestProxyConfig("${echo.base}/", "timeout", "accept",
            "content-type");


    @Test
    void method_04() {
        final var captor = TestUtil.newCaptor(MethodTestCase001.class);

        captor.proxy().get();

        Assertions.assertEquals("GET",
                new MethodParsingRequestBuilder(captor.invocation().method(), proxyConfig, resolver)
                        .apply(captor.invocation().args()).method());
    }

    @Test
    void method_05() {
        final var captor = TestUtil.newCaptor(MethodTestCase001.class);

        captor.proxy().query();

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new MethodParsingRequestBuilder(captor.invocation().method(), proxyConfig, resolver)
                        .apply(captor.invocation().args()));
    }

    @Test
    void method_06() {
        final var captor = TestUtil.newCaptor(MethodTestCase001.class);

        captor.proxy().post();

        Assertions.assertEquals("POST",
                new MethodParsingRequestBuilder(captor.invocation().method(), proxyConfig, resolver)
                        .apply(captor.invocation().args()).method());
    }

    @Test
    void method_07() {
        final var captor = TestUtil.newCaptor(MethodTestCase001.class);

        captor.proxy().delete();

        Assertions.assertEquals("DELETE",
                new MethodParsingRequestBuilder(captor.invocation().method(), proxyConfig, resolver)
                        .apply(captor.invocation().args()).method());
    }

    @Test
    void method_08() {
        final var captor = TestUtil.newCaptor(MethodTestCase001.class);

        captor.proxy().put();

        final var request = new MethodParsingRequestBuilder(captor.invocation().method(), proxyConfig, resolver)
                .apply(captor.invocation().args());

        Assertions.assertEquals("PUT", request.method());
    }

    @Test
    void method_10() {
        final var captor = TestUtil.newCaptor(MethodTestCase001.class);

        captor.proxy().patch();

        Assertions.assertEquals("PATCH",
                new MethodParsingRequestBuilder(captor.invocation().method(), proxyConfig, resolver)
                        .apply(captor.invocation().args()).method());
    }

    @Test
    void method_11() {
        final var captor = TestUtil.newCaptor(MethodTestCase001.class);

        captor.proxy().create();

        Assertions.assertEquals("POST",
                new MethodParsingRequestBuilder(captor.invocation().method(), proxyConfig, resolver)
                        .apply(captor.invocation().args()).method());
    }

    @Test
    void method_12() {
        final var captor = TestUtil.newCaptor(MethodTestCase001.class);

        captor.proxy().remove();

        Assertions.assertEquals("DELETE",
                new MethodParsingRequestBuilder(captor.invocation().method(), proxyConfig, resolver)
                        .apply(captor.invocation().args()).method());
    }

    @Test
    void method_13() {
        final var captor = TestUtil.newCaptor(MethodTestCase001.class);

        captor.proxy().getBySomething();

        Assertions.assertEquals("GET",
                new MethodParsingRequestBuilder(captor.invocation().method(), proxyConfig, resolver)
                        .apply(captor.invocation().args()).method());
    }

    @Test
    void method_14() {
        final var captor = TestUtil.newCaptor(MethodTestCase001.class);

        captor.proxy().postByName();

        Assertions.assertEquals("POST",
                new MethodParsingRequestBuilder(captor.invocation().method(), proxyConfig, resolver)
                        .apply(captor.invocation().args()).method());
    }

    @Test
    void uri_01() {
        final var captor = TestUtil.newCaptor(UriTestCase001.class);

        captor.proxy().get();

        Assertions.assertEquals("${echo.base}/get",
                new MethodParsingRequestBuilder(captor.invocation().method(), proxyConfig, resolver)
                        .apply(captor.invocation().args()).uri());
    }

    @Test
    void uri_02() {
        final String[] ref = new String[1];
        final var captor = TestUtil.newCaptor(UriTestCase001.class);

        captor.proxy().get();

        Assertions.assertEquals("123",
                new MethodParsingRequestBuilder(captor.invocation().method(), proxyConfig, s -> {
                    ref[0] = s;
                    return "123";
                }).apply(captor.invocation().args()).uri());

        Assertions.assertEquals("${echo.base}/get", ref[0]);
    }

    @Test
    void uri_03() {
        final var captor = TestUtil.newCaptor(UriTestCase001.class);

        captor.proxy().get("");

        Assertions.assertEquals("${echo.base}/get1",
                new MethodParsingRequestBuilder(captor.invocation().method(), proxyConfig, resolver)
                        .apply(captor.invocation().args()).uri());
    }

    @Test
    void uri_04() {
        final var captor = TestUtil.newCaptor(UriTestCase001.class);

        captor.proxy().get(1);

        Assertions.assertEquals("${echo.base}/",
                new MethodParsingRequestBuilder(captor.invocation().method(), proxyConfig, resolver)
                        .apply(captor.invocation().args()).uri());
    }
}
