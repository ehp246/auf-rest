package me.ehp246.aufrest.core.byrest;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import me.ehp246.aufrest.api.rest.AuthScheme;
import me.ehp246.aufrest.api.rest.ByRestProxyConfig;
import me.ehp246.aufrest.api.rest.HttpUtils;
import me.ehp246.aufrest.api.spi.InvocationAuthProviderResolver;
import me.ehp246.aufrest.api.spi.PropertyResolver;
import me.ehp246.test.TestUtil;

/**
 * @author Lei Yang
 *
 */
class ProxyMethodParserTest {
    private final InvocationAuthProviderResolver invocationAuthResolver = n -> null;
    private final PropertyResolver mockResolver = new MockEnvironment().withProperty("echo.base",
            "http://localhost")::resolveRequiredPlaceholders;
    private final ByRestProxyConfig proxyConfig = new ByRestProxyConfig("${echo.base}/", "timeout", "accept-i",
            "content-type-i");
    private final ByRestProxyConfig basicAuthConfig = new ByRestProxyConfig("${echo.base}/",
            new ByRestProxyConfig.AuthConfig(List.of("postman", "password"), AuthScheme.BASIC), "timeout", "accept-i",
            "content-type-i", true, Object.class, "");

    private final ProxyMethodParser parser = new ProxyMethodParser(mockResolver, invocationAuthResolver);

    @Test
    void method_04() {
        final var captor = TestUtil.newCaptor(MethodTestCase001.class);

        captor.proxy().get();

        Assertions.assertEquals("GET", new ProxyMethodParser(mockResolver, invocationAuthResolver)
                .parse(captor.invocation().method(), proxyConfig).apply(captor.invocation().args()).method());
    }

    @Test
    void method_05() {
        final var captor = TestUtil.newCaptor(MethodTestCase001.class);

        captor.proxy().query();

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new ProxyMethodParser(mockResolver, invocationAuthResolver)
                        .parse(captor.invocation().method(), proxyConfig).apply(captor.invocation().args()));
    }

    @Test
    void method_06() {
        final var captor = TestUtil.newCaptor(MethodTestCase001.class);

        captor.proxy().post();

        Assertions.assertEquals("POST", new ProxyMethodParser(mockResolver, invocationAuthResolver)
                .parse(captor.invocation().method(), proxyConfig).apply(captor.invocation().args()).method());
    }

    @Test
    void method_07() {
        final var captor = TestUtil.newCaptor(MethodTestCase001.class);

        captor.proxy().delete();

        Assertions.assertEquals("DELETE", new ProxyMethodParser(mockResolver, invocationAuthResolver)
                .parse(captor.invocation().method(), proxyConfig).apply(captor.invocation().args()).method());
    }

    @Test
    void method_08() {
        final var captor = TestUtil.newCaptor(MethodTestCase001.class);

        captor.proxy().put();

        final var request = new ProxyMethodParser(mockResolver, invocationAuthResolver)
                .parse(captor.invocation().method(), proxyConfig).apply(captor.invocation().args());

        Assertions.assertEquals("PUT", request.method());
    }

    @Test
    void method_10() {
        final var captor = TestUtil.newCaptor(MethodTestCase001.class);

        captor.proxy().patch();

        Assertions.assertEquals("PATCH",
                new ProxyMethodParser(mockResolver, invocationAuthResolver)
                        .parse(captor.invocation().method(), proxyConfig)

                        .apply(captor.invocation().args()).method());
    }

    @Test
    void method_11() {
        final var captor = TestUtil.newCaptor(MethodTestCase001.class);

        captor.proxy().create();

        Assertions.assertEquals("POST", new ProxyMethodParser(mockResolver, invocationAuthResolver)
                .parse(captor.invocation().method(), proxyConfig).apply(captor.invocation().args()).method());
    }

    @Test
    void method_12() {
        final var captor = TestUtil.newCaptor(MethodTestCase001.class);

        captor.proxy().remove();

        Assertions.assertEquals("DELETE", new ProxyMethodParser(mockResolver, invocationAuthResolver)
                .parse(captor.invocation().method(), proxyConfig).apply(captor.invocation().args()).method());
    }

    @Test
    void method_13() {
        final var captor = TestUtil.newCaptor(MethodTestCase001.class);

        captor.proxy().getBySomething();

        Assertions.assertEquals("GET",
                new ProxyMethodParser(mockResolver, invocationAuthResolver)
                        .parse(captor.invocation().method(), proxyConfig)

                        .apply(captor.invocation().args()).method());
    }

    @Test
    void method_14() {
        final var captor = TestUtil.newCaptor(MethodTestCase001.class);

        captor.proxy().postByName();

        Assertions.assertEquals("POST", new ProxyMethodParser(mockResolver, invocationAuthResolver)
                .parse(captor.invocation().method(), proxyConfig).apply(captor.invocation().args()).method());
    }

    @Test
    void uri_01() {
        final var captor = TestUtil.newCaptor(UriTestCase001.class);

        captor.proxy().get();

        Assertions.assertEquals("http://localhost/get",
                new ProxyMethodParser(mockResolver, invocationAuthResolver)
                        .parse(captor.invocation().method(), proxyConfig)

                        .apply(captor.invocation().args()).uri());
    }

    @Test
    void uri_03() {
        final var captor = TestUtil.newCaptor(UriTestCase001.class);

        captor.proxy().get("");

        Assertions.assertEquals("http://localhost/get1", new ProxyMethodParser(mockResolver, invocationAuthResolver)
                .parse(captor.invocation().method(), proxyConfig).apply(captor.invocation().args()).uri());
    }

    @Test
    void uri_04() {
        final var captor = TestUtil.newCaptor(UriTestCase001.class);

        captor.proxy().get(1);

        Assertions.assertEquals("http://localhost/", new ProxyMethodParser(mockResolver, invocationAuthResolver)
                .parse(captor.invocation().method(), proxyConfig).apply(captor.invocation().args()).uri());
    }

    @Test
    void path_01() {
        final var captor = TestUtil.newCaptor(PathTestCase001.class);

        captor.proxy().get("1", "3");

        Assertions.assertEquals("http://localhost/get/1/path2/3",
                new ProxyMethodParser(mockResolver, invocationAuthResolver)
                        .parse(captor.invocation().method(), proxyConfig).apply(captor.invocation().args()).uri());
    }

    @Test
    void path_02() {
        final var captor = TestUtil.newCaptor(PathTestCase001.class);

        captor.proxy().get("4", "1", "3");

        /**
         * Method-level annotation overwrites type-level. This behavior is different
         * from Spring's RequestMapping.
         */
        Assertions.assertEquals("http://localhost/3/4", new ProxyMethodParser(mockResolver, invocationAuthResolver)
                .parse(captor.invocation().method(), proxyConfig).apply(captor.invocation().args()).uri());
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
                new ProxyMethodParser(mockResolver, invocationAuthResolver)
                        .parse(captor.invocation().method(), proxyConfig).apply(captor.invocation().args()).uri());
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
                new ProxyMethodParser(mockResolver, invocationAuthResolver)
                        .parse(captor.invocation().method(), proxyConfig).apply(captor.invocation().args()).uri());
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
                new ProxyMethodParser(mockResolver, invocationAuthResolver)
                        .parse(captor.invocation().method(), proxyConfig).apply(captor.invocation().args()).uri());
    }

    @Test
    void path_06() {
        final var captor = TestUtil.newCaptor(PathTestCase001.class);

        captor.proxy().get();

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new ProxyMethodParser(mockResolver, invocationAuthResolver)
                        .parse(captor.invocation().method(), proxyConfig)

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
                () -> new ProxyMethodParser(mockResolver, invocationAuthResolver)
                        .parse(captor.invocation().method(), proxyConfig).apply(captor.invocation().args()).uri());
    }

    @Test
    void path_08() {
        final var captor = TestUtil.newCaptor(PathTestCase001.class);

        captor.proxy().get("", "");

        Assertions.assertEquals("http://localhost/get//path2/",
                new ProxyMethodParser(mockResolver, invocationAuthResolver)
                        .parse(captor.invocation().method(), proxyConfig)

                        .apply(captor.invocation().args()).uri());
    }

    @Test
    void queryParams_01() {
        final var captor = TestUtil.newCaptor(RequestParamCase001.class);

        captor.proxy().getByParams("q1", "q2");

        final var request = new ProxyMethodParser(mockResolver, invocationAuthResolver)
                .parse(captor.invocation().method(), proxyConfig).apply(captor.invocation().args());
        final var queryParams = request.queryParams();

        Assertions.assertEquals(2, queryParams.size());

        Assertions.assertEquals(1, queryParams.get("query1").size());
        Assertions.assertEquals("q1", queryParams.get("query1").get(0));

        Assertions.assertEquals(1, queryParams.get("query2").size());
        Assertions.assertEquals("q2", queryParams.get("query2").get(0));

        Assertions.assertEquals(true, request.queryParams() == queryParams);
    }

    @Test
    void queryParams_02() {
        final var captor = TestUtil.newCaptor(RequestParamCase001.class);

        captor.proxy().queryEncoded("1 + 1 = 2");

        Assertions.assertEquals("1 + 1 = 2",
                new ProxyMethodParser(mockResolver, invocationAuthResolver)
                        .parse(captor.invocation().method(), proxyConfig).apply(captor.invocation().args())
                        .queryParams().get("query 1").get(0),
                "should not need to encode");
    }

    @Test
    void queryParams_03() {
        final var captor = TestUtil.newCaptor(RequestParamCase001.class);

        captor.proxy().getRepeated("1 + 1 = 2", "3");

        final var queryParams = new ProxyMethodParser(mockResolver, invocationAuthResolver)
                .parse(captor.invocation().method(), proxyConfig).apply(captor.invocation().args()).queryParams();

        Assertions.assertEquals(2, queryParams.get("query 1").size());
        Assertions.assertEquals("1 + 1 = 2", queryParams.get("query 1").get(0));
        Assertions.assertEquals("3", queryParams.get("query 1").get(1));
    }

    @Test
    void queryParams_04() {
        final var captor = TestUtil.newCaptor(RequestParamCase001.class);

        captor.proxy().getByList(List.of("1 + 1 = 2", "3"));

        final var queryParams = new ProxyMethodParser(mockResolver, invocationAuthResolver)
                .parse(captor.invocation().method(), proxyConfig)

                .apply(captor.invocation().args()).queryParams();

        Assertions.assertEquals(1, queryParams.size());

        Assertions.assertEquals("1 + 1 = 2", queryParams.get("qList").get(0));
        Assertions.assertEquals("3", queryParams.get("qList").get(1));
    }

    @Test
    void queryParams_05() {
        final var captor = TestUtil.newCaptor(RequestParamCase001.class);

        captor.proxy().getByMap(Map.of("query 1", "1 + 1 = 2", "query2", "q2"));

        final var queryParams = new ProxyMethodParser(mockResolver, invocationAuthResolver)
                .parse(captor.invocation().method(), proxyConfig).apply(captor.invocation().args()).queryParams();

        Assertions.assertEquals(2, queryParams.size());
        Assertions.assertEquals("1 + 1 = 2", queryParams.get("query 1").get(0));
        Assertions.assertEquals("q2", queryParams.get("query2").get(0));
    }

    @Test
    void queryParams_06() {
        final var captor = TestUtil.newCaptor(RequestParamCase001.class);

        captor.proxy().getByMap(Map.of("query 1", "1 + 1 = 2", "query2", "q2-a"), "q2-b");

        final var queryParams = new ProxyMethodParser(mockResolver, invocationAuthResolver)
                .parse(captor.invocation().method(), proxyConfig).apply(captor.invocation().args()).queryParams();

        Assertions.assertEquals(2, queryParams.size());
        Assertions.assertEquals(1, queryParams.get("query 1").size());
        Assertions.assertEquals("1 + 1 = 2", queryParams.get("query 1").get(0));

        Assertions.assertEquals(2, queryParams.get("query2").size(), "Should collect all");
        Assertions.assertEquals("q2-a", queryParams.get("query2").get(0));
        Assertions.assertEquals("q2-b", queryParams.get("query2").get(1));
    }

    @Test
    void acceptGZip_01() {
        final var captor = TestUtil.newCaptor(AcceptGZipTestCase001.class);

        captor.proxy().get();

        final var accept = new ProxyMethodParser(mockResolver, invocationAuthResolver)
                .parse(captor.invocation().method(), proxyConfig).apply(captor.invocation().args());

        Assertions.assertEquals(true, accept.headers().get("accept-encoding").get(0).equalsIgnoreCase("gzip"));
    }

    @Test
    void acceptGZip_02() {
        final var captor = TestUtil.newCaptor(AcceptGZipTestCase001.class);

        captor.proxy().get();

        final var request = new ProxyMethodParser(mockResolver, invocationAuthResolver)
                .parse(captor.invocation().method(),
                        new ByRestProxyConfig("", null, null, null, null, false, null, null))
                .apply(captor.invocation().args());

        Assertions.assertEquals(null, request.headers().get("accept-encoding"));
    }

    @Test
    void contentType_01() {
        final var captor = TestUtil.newCaptor(ContentTypeTestCase01.class);

        captor.proxy().get1();

        final var request = new ProxyMethodParser(mockResolver, invocationAuthResolver)
                .parse(captor.invocation().method(), proxyConfig).apply(captor.invocation().args());

        Assertions.assertEquals(proxyConfig.contentType(), request.contentType());
        Assertions.assertEquals(proxyConfig.accept(), request.accept());
    }

    @Test
    void contentType_02() {
        final var captor = TestUtil.newCaptor(ContentTypeTestCase01.class);

        captor.proxy().get2();

        final var request = new ProxyMethodParser(mockResolver, invocationAuthResolver)
                .parse(captor.invocation().method(), proxyConfig).apply(captor.invocation().args());

        Assertions.assertEquals(proxyConfig.contentType(), request.contentType());
        Assertions.assertEquals(HttpUtils.APPLICATION_JSON, request.accept());
    }

    @Test
    void contentType_03() {
        final var captor = TestUtil.newCaptor(ContentTypeTestCase01.class);

        captor.proxy().get3();

        final var request = new ProxyMethodParser(mockResolver, invocationAuthResolver)
                .parse(captor.invocation().method(), proxyConfig).apply(captor.invocation().args());

        Assertions.assertEquals("m-type", request.contentType());
        Assertions.assertEquals("m-accept", request.accept());
    }

    @Test
    void auth_global_01() {
        final var captor = TestUtil.newCaptor(AuthTestCases.Case001.class);

        captor.proxy().get();

        final var request = parser.parse(captor.invocation().method(), proxyConfig).apply(captor.invocation().args());

        Assertions.assertEquals(null, request.authSupplier(),
                "Should have no supplier leaving it to the global provider");
    }

    @Test
    void auth_basic_01() {
        final var captor = TestUtil.newCaptor(AuthTestCases.Case001.class);

        captor.proxy().get();

        Assertions.assertEquals("Basic cG9zdG1hbjpwYXNzd29yZA==",
                parser.parse(captor.invocation().method(), basicAuthConfig).apply(captor.invocation().args())
                        .authSupplier().get());
    }

    @Test
    void auth_bearer_01() {
        final var captor = TestUtil.newCaptor(AuthTestCases.Case001.class);

        captor.proxy().get();

        Assertions
                .assertEquals(
                        "Bearer ec3fb099-7fa3-477b-82ce-05547babad95", parser
                                .parse(captor.invocation().method(),
                                        new ByRestProxyConfig("${echo.base}/", new ByRestProxyConfig.AuthConfig(
                                                List.of("ec3fb099-7fa3-477b-82ce-05547babad95"), AuthScheme.BEARER),
                                                "timeout", "accept-i", "content-type-i", true, Object.class, ""))
                                .apply(captor.invocation().args()).authSupplier().get());
    }

    @Test
    void auth_none_01() {
        final var captor = TestUtil.newCaptor(AuthTestCases.Case001.class);

        captor.proxy().get();

        Assertions.assertEquals(null, parser.parse(captor.invocation().method(),
                new ByRestProxyConfig("${echo.base}/", new ByRestProxyConfig.AuthConfig(List.of(), AuthScheme.NONE),
                        "timeout", "accept-i", "content-type-i", true, Object.class, ""))
                .apply(captor.invocation().args()).authSupplier().get());
    }

    @Test
    void auth_simple_01() {
        final var captor = TestUtil.newCaptor(AuthTestCases.Case001.class);

        captor.proxy().get();

        Assertions.assertEquals("CustomKey custom.header.123", parser.parse(captor.invocation().method(),
                new ByRestProxyConfig("${echo.base}/", new ByRestProxyConfig.AuthConfig(
                        List.of("CustomKey custom.header.123"),
                                                        AuthScheme.SIMPLE),
                                                "timeout", "accept-i", "content-type-i", true, Object.class, ""))
                                .apply(captor.invocation().args()).authSupplier().get());
    }

    @Test
    void authHeader_01() {
        final var captor = TestUtil.newCaptor(AuthTestCases.Case001.class);
        final var expected = UUID.randomUUID().toString();

        captor.proxy().get(expected);

        final var request = parser.parse(captor.invocation().method(), proxyConfig).apply(captor.invocation().args());

        Assertions.assertEquals(expected, request.authSupplier().get());
    }

    @Test
    void authHeader_02() {
        final var captor = TestUtil.newCaptor(AuthTestCases.Case001.class);
        final var expected = (String) null;

        captor.proxy().get(expected);

        final var request = parser.parse(captor.invocation().method(), proxyConfig).apply(captor.invocation().args());

        Assertions.assertEquals(expected, request.authSupplier().get());
    }

    @Test
    void authHeader_03() {
        final var captor = TestUtil.newCaptor(AuthTestCases.Case001.class);
        final var expected = (Supplier<String>) () -> null;

        captor.proxy().get(expected);

        final var request = parser.parse(captor.invocation().method(), proxyConfig).apply(captor.invocation().args());

        Assertions.assertEquals(expected, request.authSupplier());
    }

    @Test
    void authHeader_04() {
        final var captor = TestUtil.newCaptor(AuthTestCases.Case001.class);

        captor.proxy().getFailure(null, null);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> parser.parse(captor.invocation().method(), proxyConfig));
    }
}
