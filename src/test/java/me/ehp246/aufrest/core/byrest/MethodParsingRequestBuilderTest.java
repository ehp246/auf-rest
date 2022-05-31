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
    private final ByRestProxyConfig proxyConfig = new ByRestProxyConfig("uri", "timeout", "accept", "content-type");

    @Test
    void method_01() {
        final var captor = TestUtil.newCaptor(GetCase001.class);

        captor.proxy().get();

        final var request = MethodParsingRequestBuilder.parse(captor.invocation().method(), proxyConfig, resolver)
                .apply(captor.invocation().args());

        Assertions.assertEquals("GET", request.method());
    }

    @Test
    void method_02() {
        final var captor = TestUtil.newCaptor(GetCase001.class);

        captor.proxy().get("");

        final var request = MethodParsingRequestBuilder.parse(captor.invocation().method(), proxyConfig, resolver)
                .apply(captor.invocation().args());

        Assertions.assertEquals("GET", request.method());
    }

    @Test
    void method_03() {
        final var captor = TestUtil.newCaptor(GetCase001.class);

        captor.proxy().get(0);

        final var request = MethodParsingRequestBuilder.parse(captor.invocation().method(), proxyConfig, resolver)
                .apply(captor.invocation().args());

        Assertions.assertEquals("GET", request.method());
    }

    @Test
    void method_04() {
        final var captor = TestUtil.newCaptor(MethodTestCase001.class);

        captor.proxy().get();

        final var request = MethodParsingRequestBuilder.parse(captor.invocation().method(), proxyConfig, resolver)
                .apply(captor.invocation().args());

        Assertions.assertEquals("GET", request.method());
    }

    @Test
    void method_05() {
        final var captor = TestUtil.newCaptor(MethodTestCase001.class);

        captor.proxy().query();

        Assertions.assertThrows(IllegalArgumentException.class, () -> MethodParsingRequestBuilder.parse(captor.invocation().method(), proxyConfig, resolver)
                .apply(captor.invocation().args()));
    }

    @Test
    void method_06() {
        final var captor = TestUtil.newCaptor(MethodTestCase001.class);

        captor.proxy().post();

        final var request = MethodParsingRequestBuilder.parse(captor.invocation().method(), proxyConfig, resolver)
                .apply(captor.invocation().args());

        Assertions.assertEquals("POST", request.method());
    }

    @Test
    void method_07() {
        final var captor = TestUtil.newCaptor(MethodTestCase001.class);

        captor.proxy().delete();

        final var request = MethodParsingRequestBuilder.parse(captor.invocation().method(), proxyConfig, resolver)
                .apply(captor.invocation().args());

        Assertions.assertEquals("DELETE", request.method());
    }

    @Test
    void method_08() {
        final var captor = TestUtil.newCaptor(MethodTestCase001.class);

        captor.proxy().put();

        final var request = MethodParsingRequestBuilder.parse(captor.invocation().method(), proxyConfig, resolver)
                .apply(captor.invocation().args());

        Assertions.assertEquals("PUT", request.method());
    }

    @Test
    void method_10() {
        final var captor = TestUtil.newCaptor(MethodTestCase001.class);

        captor.proxy().patch();

        final var request = MethodParsingRequestBuilder.parse(captor.invocation().method(), proxyConfig, resolver)
                .apply(captor.invocation().args());

        Assertions.assertEquals("PATCH", request.method());
    }
}
