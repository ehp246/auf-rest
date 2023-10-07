package me.ehp246.aufrest.core.rest;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.web.bind.annotation.RequestMethod;

import me.ehp246.aufrest.api.exception.RestFnException;
import me.ehp246.aufrest.api.rest.HttpUtils;
import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.api.spi.PropertyResolver;
import me.ehp246.aufrest.core.rest.AuthTestCases.BasicAuthCase01;
import me.ehp246.aufrest.core.rest.AuthTestCases.BasicAuthCase02;
import me.ehp246.aufrest.core.rest.AuthTestCases.BearerAuthCase01;
import me.ehp246.aufrest.core.rest.AuthTestCases.BearerAuthCase02;
import me.ehp246.aufrest.core.rest.AuthTestCases.SimpleAuthCase01;
import me.ehp246.aufrest.core.rest.AuthTestCases.SimpleAuthCase02;
import me.ehp246.aufrest.core.rest.QueryParamCases.Case01;
import me.ehp246.aufrest.core.rest.UriCases.Uri01;
import me.ehp246.aufrest.core.rest.UriCases.Uri02;
import me.ehp246.test.mock.MockBodyHandlerProvider;
import me.ehp246.test.mock.MockHttpResponse;
import me.ehp246.test.mock.MockRestFn;
import me.ehp246.test.mock.MockRestFnProvider;

/**
 * @author Lei Yang
 *
 */
class ByRestProxyFactoryTest {
    private final MockRestFn restFn = new MockRestFn();

    private final PropertyResolver propertyResolver = new MockEnvironment()
            .withProperty("echo.base", "https://localhost")
            .withProperty("api.bearer.token", "ec3fb099-7fa3-477b-82ce-05547babad95")
            .withProperty("postman.username", "postman")
            .withProperty("postman.password", "password")::resolveRequiredPlaceholders;

    private final ProxyMethodParser parser = new DefaultProxyMethodParser(propertyResolver, name -> null,
            name -> r -> null, new MockBodyHandlerProvider());

    private final ByRestProxyFactory factory = new ByRestProxyFactory(restFn.toProvider(), parser);

    @BeforeEach
    void beforeEach() {
        restFn.clearReq();
    }

    @Test
    void default_01() {
        final var newInstance = factory.newInstance(GetCase01.class);

        Assertions.assertEquals(true, newInstance.hashCode() == newInstance.hashCode());
        Assertions.assertEquals(true, newInstance.equals(List.of(newInstance).get(0)));
        Assertions.assertEquals(true, Set.of(newInstance).contains(newInstance));
        Assertions.assertEquals(true, newInstance instanceof GetCase01);
        Assertions.assertEquals(true, !newInstance.toString().isBlank());
        Assertions.assertEquals(1, newInstance.getInc(0));
    }

    @Test
    void method_01() {
        final var newInstance = factory.newInstance(GetCase01.class);

        newInstance.get();

        final var request = restFn.req();

        Assertions.assertEquals("GET", request.method().toUpperCase());
    }

    @Test
    void method_02() {
        final var newInstance = factory.newInstance(GetCase01.class);

        newInstance.get("");

        final var request = restFn.req();

        Assertions.assertEquals("GET", request.method().toUpperCase());
    }

    @Test
    void method_03() {
        final var newInstance = factory.newInstance(GetCase01.class);

        newInstance.get(0);

        Assertions.assertEquals("GET", restFn.req().method().toUpperCase());
    }

    @Test
    void method_04() {
        factory.newInstance(MethodTestCase01.class).get();

        Assertions.assertEquals("GET", restFn.req().method());
    }

    @Test
    void method_05() {
        Assertions.assertThrows(Exception.class, factory.newInstance(MethodTestCase01.class)::query);
    }

    @Test
    void method_06() {
        factory.newInstance(MethodTestCase01.class).post();

        Assertions.assertEquals("POST", restFn.req().method());
    }

    @Test
    void method_07() {
        factory.newInstance(MethodTestCase01.class).delete();

        Assertions.assertEquals(RequestMethod.DELETE.name(), restFn.req().method());
    }

    @Test
    void method_08() {
        factory.newInstance(MethodTestCase01.class).put();

        Assertions.assertEquals(RequestMethod.PUT.name(), restFn.req().method());
    }

    @Test
    void method_09() {
        factory.newInstance(MethodTestCase01.class).patch();

        Assertions.assertEquals(RequestMethod.PATCH.name(), restFn.req().method());
    }

    @Test
    void method_10() {
        Assertions.assertThrows(Exception.class, factory.newInstance(MethodTestCase01.class)::query);
    }

    @Test
    void method_11() {
        factory.newInstance(MethodTestCase01.class).create();

        Assertions.assertEquals("POST", restFn.req().method());
    }

    @Test
    void method_12() {
        factory.newInstance(MethodTestCase01.class).remove();

        Assertions.assertEquals("DELETE", restFn.req().method());
    }

    @Test
    void method_13() {
        factory.newInstance(MethodTestCase01.class).getBySomething();

        Assertions.assertEquals("GET", restFn.req().method());
    }

    @Test
    void method_14() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> factory.newInstance(MethodTestCase01.class).query(1));
    }

    @Test
    void method_15() {
        factory.newInstance(MethodTestCase01.class).postByName();

        Assertions.assertEquals("POST", restFn.req().method());
    }

    @Test
    void queryParams_01() {
        factory.newInstance(Case01.class).getByParams("q1", "q2");

        final var request = restFn.req();

        Assertions.assertEquals("", request.contentType());
        Assertions.assertEquals("https://localhost/get", request.uri());

        final var queryParams = request.queries();
        Assertions.assertEquals(2, queryParams.size());

        Assertions.assertEquals(1, queryParams.get("query1").size());
        Assertions.assertEquals("q1", queryParams.get("query1").get(0));

        Assertions.assertEquals(1, queryParams.get("query2").size());
        Assertions.assertEquals("q2", queryParams.get("query2").get(0));
    }

    @Test
    void queryParams_02() {
        factory.newInstance(Case01.class).get("1 + 1 = 2");

        Assertions.assertEquals("1 + 1 = 2", restFn.req().queries().get("query 1").get(0), "Should not encode");
    }

    @Test
    void queryParams_03() {
        factory.newInstance(Case01.class).getByMultiple("1 + 1 = 2", "3");

        final var queryParams = restFn.req().queries();

        Assertions.assertEquals(1, queryParams.size());

        Assertions.assertEquals("1 + 1 = 2", queryParams.get("query 1").get(0));
        Assertions.assertEquals("3", queryParams.get("query 1").get(1));
    }

    @Test
    void queryParams_04() {
        factory.newInstance(QueryParamCases.Case02.class).get();

        final var queryParams = restFn.req().queries();

        Assertions.assertEquals("ec3fb099-7fa3-477b-82ce-05547babad95", queryParams.get("query2").get(0));
        Assertions.assertEquals("08dda6c5-e80f-44ef-b0cb-d9c261bf8352", queryParams.get("query3").get(0));
        Assertions.assertEquals("08dda6c5-e80f-44ef-b0cb-d9c261bf8353", queryParams.get("query3").get(1));
    }

    @Test
    void queryParams_05() {
        final var query1 = UUID.randomUUID().toString();
        factory.newInstance(QueryParamCases.Case02.class).getByParams(query1);

        final var queryParams = restFn.req().queries();

        Assertions.assertEquals(query1, queryParams.get("query1").get(0));
        Assertions.assertEquals("ec3fb099-7fa3-477b-82ce-05547babad95", queryParams.get("query2").get(0));
        Assertions.assertEquals("08dda6c5-e80f-44ef-b0cb-d9c261bf8352", queryParams.get("query3").get(0));
        Assertions.assertEquals("08dda6c5-e80f-44ef-b0cb-d9c261bf8353", queryParams.get("query3").get(1));
    }

    @Test
    void queryParams_06() {
        final var query1 = UUID.randomUUID().toString();
        final var query2 = UUID.randomUUID().toString();
        factory.newInstance(QueryParamCases.Case02.class).getByMap(Map.of("query1", query1, "query2", query2));

        final var queryParams = restFn.req().queries();

        Assertions.assertEquals(1, queryParams.get("query1").size());
        Assertions.assertEquals(query1, queryParams.get("query1").get(0));

        Assertions.assertEquals(1, queryParams.get("query2").size(), "should take in the argument only");
        Assertions.assertEquals(query2, queryParams.get("query2").get(0));

        Assertions.assertEquals(2, queryParams.get("query3").size());
        Assertions.assertEquals("08dda6c5-e80f-44ef-b0cb-d9c261bf8352", queryParams.get("query3").get(0));
        Assertions.assertEquals("08dda6c5-e80f-44ef-b0cb-d9c261bf8353", queryParams.get("query3").get(1));
    }

    @Test
    void queryParams_07() {
        Assertions.assertThrows(IllegalArgumentException.class, factory.newInstance(QueryParamCases.Case03.class)::get);
    }

    @Test
    void queryParams_08() {
        final var expected = UUID.randomUUID().toString();
        factory.newInstance(QueryParamCases.Case01.class).getByParamName(expected);
        final var queryName = restFn.req().queries();

        Assertions.assertEquals(1, queryName.size());
        Assertions.assertEquals(expected, queryName.get("query1").get(0), "should default to parameter name");
    }

    @Test
    void queryParamList_01() {
        factory.newInstance(Case01.class).getByList(List.of("1 + 1 = 2", "3"));

        final var queryParams = restFn.req().queries();

        Assertions.assertEquals(1, queryParams.size());

        Assertions.assertEquals("1 + 1 = 2", queryParams.get("qList").get(0));
        Assertions.assertEquals("3", queryParams.get("qList").get(1));
    }

    @Test
    void queryParamMap_01() {
        final var newInstance = factory.newInstance(Case01.class);

        newInstance.getByMap(Map.of("query 1", "1 + 1 = 2", "query2", "q2"));

        final var request = restFn.req();

        Assertions.assertEquals(2, request.queries().size());
        Assertions.assertEquals("1 + 1 = 2", request.queries().get("query 1").get(0));
        Assertions.assertEquals("q2", request.queries().get("query2").get(0));
    }

    @Test
    void queryParamMap_02() {
        final var newInstance = factory.newInstance(Case01.class);

        newInstance.getByMap(Map.of("query 1", "1 + 1 = 2", "query2", "q2-a"), "q2-b");

        final var request = restFn.req();

        Assertions.assertEquals(2, request.queries().size());
        Assertions.assertEquals("1 + 1 = 2", request.queries().get("query 1").get(0));

        Assertions.assertEquals(2, request.queries().get("query2").size(), "Should collect all");
        Assertions.assertEquals("q2-a", request.queries().get("query2").get(0), "Should be determinstic in order");
        Assertions.assertEquals("q2-b", request.queries().get("query2").get(1));
    }

    @Test
    @Disabled
    void queryParamMap_03() {
        final var newInstance = factory.newInstance(QueryParamCases.Case01.class);

        newInstance.getByMapOfList(Map.of("query 1", List.of("1 + 1 = 2"), "query2", List.of("q2-a")));

        final var request = restFn.req();

        Assertions.assertEquals(2, request.queries().size());

        Assertions.assertEquals(1, request.queries().get("query 1").size());
        Assertions.assertEquals("1 + 1 = 2", request.queries().get("query 1").get(0));

        Assertions.assertEquals(1, request.queries().get("query2").size());
        Assertions.assertEquals("q2-a", request.queries().get("query2").get(0));
    }

    @Test
    void acceptGzip_01() {
        factory.newInstance(HeaderTestCases.HeaderCase01.class).get("1234");

        Assertions.assertTrue(restFn.req().acceptEncoding().equalsIgnoreCase("gzip"), "should have the value");
    }

    @Test
    void acceptGzip_02() {
        factory.newInstance(HeaderTestCases.AcceptGZipCase01.class).get();

        Assertions.assertTrue(restFn.req().acceptEncoding() == null, "should have not the value");
    }

    @Test
    void header_01() {
        final var newInstance = factory.newInstance(HeaderTestCases.HeaderCase01.class);

        newInstance.get("1234");

        Assertions.assertEquals("1234", restFn.req().headers().get("x-correl-id").get(0), "should have the value");
    }

    @Test
    void header_02() {
        final var newInstance = factory.newInstance(HeaderTestCases.HeaderCase01.class);

        newInstance.get("   ");

        Assertions.assertEquals("   ", restFn.req().headers().get("x-correl-id").get(0));

        restFn.clearReq();

        newInstance.get((String) null);

        Assertions.assertEquals(0, restFn.req().headers().size());
    }

    @Test
    void header_03() {
        final var newInstance = factory.newInstance(HeaderTestCases.HeaderCase01.class);

        newInstance.get((String) null);

        Assertions.assertEquals(null, restFn.req().headers().get("x-correl-id"));
    }

    @Test
    void header_04() {
        final var newInstance = factory.newInstance(HeaderTestCases.HeaderCase01.class);

        newInstance.getBlank("1234");

        Assertions.assertEquals("1234", restFn.req().headers().get("correlid").get(0), "should take it as is");
    }

    @Test
    void header_05() {
        final var newInstance = factory.newInstance(HeaderTestCases.HeaderCase01.class);

        final var uuid = UUID.randomUUID();

        newInstance.get(uuid);

        Assertions.assertEquals(uuid.toString(), restFn.req().headers().get("x-uuid").get(0),
                "should have call toString");
    }

    @Test
    void header_07() {
        final var newInstance = factory.newInstance(HeaderTestCases.HeaderCase01.class);

        newInstance.getMultiple("1", "2");

        final var headers = restFn.req().headers();

        Assertions.assertEquals(2, headers.size(), "should have both");
        Assertions.assertEquals("1", headers.get("x-span-id").get(0));
        Assertions.assertEquals("2", headers.get("x-trace-id").get(0));
    }

    @Test
    void contentType_001() {
        final var newInstance = factory.newInstance(ContentTypeTestCases.Case001.class);

        newInstance.get1();

        final var req = restFn.req();

        Assertions.assertEquals("i-type", req.contentType());
        Assertions.assertEquals("i-accept", req.accept());
    }

    @Test
    void contentType_02() {
        final var newInstance = factory.newInstance(ContentTypeTestCases.Case001.class);

        newInstance.get2();

        final var req = restFn.req();

        Assertions.assertEquals("i-type", req.contentType());
        Assertions.assertEquals(HttpUtils.APPLICATION_JSON, req.accept());
    }

    @Test
    void contentType_003() {
        final var newInstance = factory.newInstance(ContentTypeTestCases.Case001.class);

        newInstance.get3();

        final var req = restFn.req();

        Assertions.assertEquals("m-type", req.contentType());
        Assertions.assertEquals("m-accept", req.accept());
    }

    @Test
    void contentType_004() {
        final var newInstance = factory.newInstance(ContentTypeTestCases.Case002.class);

        newInstance.get1();

        final var req = restFn.req();

        Assertions.assertEquals("i-type", req.contentType());
        Assertions.assertEquals(HttpUtils.APPLICATION_JSON, req.accept());
    }

    @Test
    void contentType_05() {
        final var newInstance = factory.newInstance(ContentTypeTestCases.Case002.class);

        newInstance.get2();

        final var req = restFn.req();

        Assertions.assertEquals("i-type", req.contentType());
        Assertions.assertEquals(HttpUtils.APPLICATION_JSON, req.accept());
    }

    @Test
    void contentType_06() {
        final var newInstance = factory.newInstance(ContentTypeTestCases.Case002.class);

        newInstance.get3();

        final var req = restFn.req();

        Assertions.assertEquals("m-type", req.contentType());
        Assertions.assertEquals("m-accept", req.accept());
    }

    @Test
    void exception_01() {
        final var checked = new IOException();
        final var restFnException = new RestFnException(checked, Mockito.mock(HttpRequest.class),
                Mockito.mock(RestRequest.class));
        final var newInstance = new ByRestProxyFactory(new MockRestFnProvider(restFnException), parser)
                .newInstance(ExceptionCase.class);

        final var thrown = Assertions.assertThrows(RestFnException.class, newInstance::get);

        Assertions.assertEquals(restFnException, thrown);
    }

    @Test
    void exception_02() {
        final var checked = new IOException();
        final var restFnException = new RestFnException(checked, Mockito.mock(HttpRequest.class),
                Mockito.mock(RestRequest.class));
        final var newInstance = new ByRestProxyFactory(new MockRestFnProvider(restFnException), parser)
                .newInstance(ExceptionCase.class);

        final var thrown = Assertions.assertThrows(IOException.class, newInstance::delete);

        Assertions.assertEquals(checked, thrown);
    }

    @Test
    void exception_03() {
        final var checked = new InterruptedException();
        final var restFnException = new RestFnException(checked, Mockito.mock(HttpRequest.class),
                Mockito.mock(RestRequest.class));
        final var newInstance = new ByRestProxyFactory(new MockRestFnProvider(restFnException), parser)
                .newInstance(ExceptionCase.class);

        final var thrown = Assertions.assertThrows(InterruptedException.class, newInstance::delete);

        Assertions.assertEquals(checked, thrown);
    }

    @Test
    void exception_04() {
        final var toBeThrown = new RuntimeException();
        final var newInstance = new ByRestProxyFactory(new MockRestFnProvider(toBeThrown), parser)
                .newInstance(ExceptionCase.class);

        final var thrown = Assertions.assertThrows(RuntimeException.class, newInstance::delete);

        Assertions.assertEquals(toBeThrown, thrown);
    }

    @Test
    void exception_05() {
        Assertions
                .assertThrows(ClassCastException.class,
                        new ByRestProxyFactory(
                                config -> new MockRestFn(new MockHttpResponse<Object>(200, Instant.now())), parser)
                                        .newInstance(ExceptionCase.class)::post);
    }

    @Test
    void authSimple_01() {
        factory.newInstance(AuthTestCases.Case004.class).get();

        Assertions.assertEquals("CustomKey custom.header.123", restFn.req().authSupplier().get());
    }

    @Test
    void authSimple_08() {
        factory.newInstance(SimpleAuthCase01.class).get();

        Assertions.assertEquals("SIMPLE", restFn.req().authSupplier().get(), "should follow the interface");
    }

    @Test
    void authSimple_10() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> factory.newInstance(SimpleAuthCase02.class).get());
    }

    @Test
    void authBasic_01() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> factory.newInstance(BasicAuthCase01.class).get());
    }

    @Test
    void authBasic_02() {
        factory.newInstance(BasicAuthCase02.class).get();

        Assertions.assertEquals("Basic dXNlcjpuYW1l", restFn.req().authSupplier().get());
    }

    @Test
    void authBasic_03() {
        factory.newInstance(AuthTestCases.Case02.class).get();

        Assertions.assertEquals("Basic cG9zdG1hbjpwYXNzd29yZA==", restFn.req().authSupplier().get());
    }

    @Test
    void authBearer_01() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> factory.newInstance(BearerAuthCase01.class).get());
    }

    @Test
    void authBearer_02() {
        factory.newInstance(BearerAuthCase02.class).get();

        Assertions.assertEquals("Bearer token", restFn.req().authSupplier().get());
    }

    @Test
    void authBearer_03() {
        factory.newInstance(AuthTestCases.Case03.class).get();

        Assertions.assertEquals("Bearer ec3fb099-7fa3-477b-82ce-05547babad95", restFn.req().authSupplier().get());
    }

    @Test
    void defaultAuth_01() {
        factory.newInstance(AuthTestCases.Case01.class).get();

        Assertions.assertEquals(null, restFn.req().authSupplier(),
                "Should have no supplier leaving it to the global provider");
    }

    @Test
    void defaultAuth_02() {
        factory.newInstance(AuthTestCases.Case01.class).get("");

        Assertions.assertEquals("", restFn.req().authSupplier().get());
    }

    @Test
    void defaultAuth_03() {
        factory.newInstance(AuthTestCases.Case01.class).get(" ");

        Assertions.assertEquals(" ", restFn.req().authSupplier().get());
    }

    @Test
    void authHeader_01() {
        factory.newInstance(AuthTestCases.Case01.class).get((String) null);

        Assertions.assertEquals(null, restFn.req().authSupplier().get());
    }

    @Test
    void authHeader_02() {
        factory.newInstance(AuthTestCases.Case03.class).get(null);

        Assertions.assertEquals(null, restFn.req().authSupplier().get());
    }

    @Test
    void authHeader_03() {
        factory.newInstance(AuthTestCases.Case004.class).get("234");

        Assertions.assertEquals("234", restFn.req().authSupplier().get());
    }

    @Test
    void authHeader_04() {
        factory.newInstance(AuthTestCases.Case05.class).get("");

        Assertions.assertEquals("", restFn.req().authSupplier().get());
    }

    @Test
    void authHeader_05() {
        factory.newInstance(AuthTestCases.Case05.class).get("  ");

        Assertions.assertEquals("  ", restFn.req().authSupplier().get());
    }

    @Test
    void authHeader_06() {
        factory.newInstance(AuthTestCases.Case05.class).get(null);

        Assertions.assertEquals(null, restFn.req().authSupplier().get());
    }

    @Test
    void authHeader_07() {
        factory.newInstance(AuthTestCases.Case10.class).get("null");

        Assertions.assertEquals("null", restFn.req().authSupplier().get());
    }

    @Test
    void authHeader_08() {
        factory.newInstance(AuthTestCases.Case10.class).get(null);

        Assertions.assertEquals(null, restFn.req().authSupplier().get());
    }

    @Test
    void authSupplier_01() {
        final Supplier<Object> expected = UUID::randomUUID;

        factory.newInstance(AuthTestCases.Case01.class).get(expected);

        Assertions.assertEquals(expected, restFn.req().authSupplier());
    }

    @Test
    void authSupplier_02() {
        final Supplier<Object> expected = null;

        factory.newInstance(AuthTestCases.Case01.class).get(expected);

        Assertions.assertEquals(expected, restFn.req().authSupplier());
    }

    @Test
    void authNone_01() {
        factory.newInstance(AuthTestCases.Case10.class).get();

        Assertions.assertEquals(null, restFn.req().authSupplier().get());
    }

    @Test
    void authException_01() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> factory.newInstance(AuthTestCases.Case07.class).get());
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> factory.newInstance(AuthTestCases.Case08.class).get());
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> factory.newInstance(AuthTestCases.Case09.class).get());
    }

    @Test
    void uri_01() {
        final Uri01 uri01s = factory.newInstance(Uri01.class);

        uri01s.getByPathVariable("1", "3");

        final var request = restFn.req();

        Assertions.assertEquals(true, request.uri() == request.uri());
        Assertions.assertEquals("https://localhost/get/{path1}/path2/{path3}", request.uri());

        final var paths = request.paths();

        Assertions.assertEquals(2, paths.size());
        Assertions.assertEquals("1", paths.get("path1"));
        Assertions.assertEquals("3", paths.get("path3"));
    }

    @Test
    void uri_03() {
        final Uri01 uri01s = factory.newInstance(Uri01.class);
        uri01s.getByPathVariable("1", "3");

        final var request = restFn.req();

        Assertions.assertEquals("https://localhost/get/{path1}/path2/{path3}", request.uri(),
                "Should overwrite type-level annotation");
    }

    @Test
    void uri_04() {
        final Uri01 uri01s = factory.newInstance(Uri01.class);
        uri01s.getWithSub();

        Assertions.assertEquals("https://localhost/get", restFn.req().uri());
    }

    @Test
    void uri_05() {
        final Uri01 testCase = factory.newInstance(Uri01.class);

        testCase.get();

        Assertions.assertEquals("https://localhost/", restFn.req().uri());
    }

    @Test
    void uri_07() {
        final Uri02 testCase = factory.newInstance(Uri02.class);

        final var root = UUID.randomUUID().toString();
        final var id = UUID.randomUUID().toString();
        testCase.get(root, id);

        Assertions.assertEquals("https://localhost/{root}/{id}", restFn.req().uri());

        final var paths = restFn.req().paths();

        Assertions.assertEquals(true, paths.size() == 2);
        Assertions.assertEquals(root, paths.get("root"));
        Assertions.assertEquals(id, paths.get("id"));
    }

    @Test
    void path_map_01() {
        final Uri01 uri01s = factory.newInstance(Uri01.class);
        final var expected = Map.of("path1", "1", "path3", "3");

        uri01s.getByMap(expected);

        final var request = restFn.req();

        Assertions.assertEquals("https://localhost/get/{path1}/path2/{path3}", request.uri());

        final var paths = restFn.req().paths();

        Assertions.assertEquals(true, paths.size() == 2);
        Assertions.assertEquals("1", paths.get("path1"));
        Assertions.assertEquals("3", paths.get("path3"));
    }

    @Test
    void path_map_02() {
        final Uri01 uri01s = factory.newInstance(Uri01.class);
        uri01s.getByMap(Map.of("path1", "mapped1", "path3", "3"), "1");

        final var paths = restFn.req().paths();

        Assertions.assertEquals(2, paths.size());
        Assertions.assertEquals("1", paths.get("path1"));
        Assertions.assertEquals("3", paths.get("path3"));
    }

    @Test
    void path_map_03() {
        final Uri01 uri01s = factory.newInstance(Uri01.class);

        uri01s.getByMap(Map.of("path1", "mapped1", "path3", "3 &= 1: / 4 ? 5:"), "1");

        final var paths = restFn.req().paths();

        Assertions.assertEquals(2, paths.size());
        Assertions.assertEquals("1", paths.get("path1"));
        Assertions.assertEquals("3 &= 1: / 4 ? 5:", paths.get("path3"), "should not encode");
    }
}
