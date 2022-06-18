package me.ehp246.aufrest.core.byrest;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import me.ehp246.aufrest.api.exception.RestFnException;
import me.ehp246.aufrest.api.rest.BindingBodyHandlerProvider;
import me.ehp246.aufrest.api.rest.HttpUtils;
import me.ehp246.aufrest.api.rest.RestClientConfig;
import me.ehp246.aufrest.api.rest.RestFn;
import me.ehp246.aufrest.api.rest.RestFnProvider;
import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.api.spi.BodyHandlerResolver;
import me.ehp246.aufrest.api.spi.PropertyResolver;
import me.ehp246.aufrest.core.byrest.AuthTestCases.BasicAuthCase01;
import me.ehp246.aufrest.core.byrest.AuthTestCases.BasicAuthCase02;
import me.ehp246.aufrest.core.byrest.AuthTestCases.BeanAuthCase05;
import me.ehp246.aufrest.core.byrest.AuthTestCases.BearerAuthCase01;
import me.ehp246.aufrest.core.byrest.AuthTestCases.BearerAuthCase02;
import me.ehp246.aufrest.core.byrest.AuthTestCases.InvocationAuthCase01;
import me.ehp246.aufrest.core.byrest.AuthTestCases.InvocationAuthCase02;
import me.ehp246.aufrest.core.byrest.AuthTestCases.InvocationAuthCase03;
import me.ehp246.aufrest.core.byrest.AuthTestCases.SimpleAuthCase01;
import me.ehp246.aufrest.core.byrest.AuthTestCases.SimpleAuthCase02;
import me.ehp246.aufrest.mock.MockHttpResponse;
import me.ehp246.aufrest.mock.MockInvocationAuthProvider;
import me.ehp246.aufrest.mock.MockInvocationAuthProviderResolver;

/**
 * @author Lei Yang
 *
 */
class ByRestProxyFactoryTest {
    private final AtomicReference<RestRequest> reqRef = new AtomicReference<>();
    private final RestFn restFn = request -> {
        reqRef.set(request);
        return Mockito.mock(HttpResponse.class);
    };
    private final RestFnProvider restFnProvider = cfg -> restFn;
    private final PropertyResolver propertyResolver = new MockEnvironment()
            .withProperty("echo.base", "https://postman-echo.com")
            .withProperty("api.bearer.token", "ec3fb099-7fa3-477b-82ce-05547babad95")
            .withProperty("postman.username", "postman")
            .withProperty("postman.password", "password")::resolveRequiredPlaceholders;
    private final RestClientConfig clientConfig = new RestClientConfig(Duration.parse("PT123S"));
    private final ProxyMethodParser parser = new DefaultProxyMethodParser(propertyResolver, name -> null,
            name -> BodyHandlers.discarding(), binding -> BodyHandlers.discarding());
    private final BodyHandlerResolver bodyHandlerResolver = name -> BodyHandlers.discarding();
    private final BindingBodyHandlerProvider bindingBodyHandlerProvider = binding -> BodyHandlers.discarding();

    private final ByRestProxyFactory factory = new ByRestProxyFactory(restFnProvider, clientConfig, parser);

    @BeforeEach
    void beforeEach() {
        reqRef.set(null);
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

        final var request = reqRef.get();

        Assertions.assertEquals("GET", request.method().toUpperCase());
    }

    @Test
    void method_02() {
        final var newInstance = factory.newInstance(GetCase01.class);

        newInstance.get("");

        final var request = reqRef.get();

        Assertions.assertEquals("GET", request.method().toUpperCase());
    }

    @Test
    void method_03() {
        final var newInstance = factory.newInstance(GetCase01.class);

        newInstance.get(0);

        Assertions.assertEquals("GET", reqRef.get().method().toUpperCase());
    }

    @Test
    void method_04() {
        factory.newInstance(MethodTestCase01.class).get();

        Assertions.assertEquals("GET", reqRef.get().method());
    }

    @Test
    void method_05() {
        Assertions.assertThrows(Exception.class, factory.newInstance(MethodTestCase01.class)::query);
    }

    @Test
    void method_06() {
        factory.newInstance(MethodTestCase01.class).post();

        Assertions.assertEquals("POST", reqRef.get().method());
    }

    @Test
    void method_07() {
        factory.newInstance(MethodTestCase01.class).delete();

        Assertions.assertEquals(RequestMethod.DELETE.name(), reqRef.get().method());
    }

    @Test
    void method_08() {
        factory.newInstance(MethodTestCase01.class).put();

        Assertions.assertEquals(RequestMethod.PUT.name(), reqRef.get().method());
    }

    @Test
    void method_09() {
        factory.newInstance(MethodTestCase01.class).patch();

        Assertions.assertEquals(RequestMethod.PATCH.name(), reqRef.get().method());
    }

    @Test
    void method_10() {
        Assertions.assertThrows(Exception.class, factory.newInstance(MethodTestCase01.class)::query);
    }

    @Test
    void method_11() {
        factory.newInstance(MethodTestCase01.class).create();

        Assertions.assertEquals("POST", reqRef.get().method());
    }

    @Test
    void method_12() {
        factory.newInstance(MethodTestCase01.class).remove();

        Assertions.assertEquals("DELETE", reqRef.get().method());
    }

    @Test
    void method_13() {
        factory.newInstance(MethodTestCase01.class).getBySomething();

        Assertions.assertEquals("GET", reqRef.get().method());
    }

    @Test
    void method_14() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> factory.newInstance(MethodTestCase01.class).query(1));
    }

    @Test
    void method_15() {
        factory.newInstance(MethodTestCase01.class).postByName();

        Assertions.assertEquals("POST", reqRef.get().method());
    }

    @Test
    void queryParams_01() {
        factory.newInstance(RequestParamCase01.class).queryByParams("q1", "q2");

        final var request = reqRef.get();

        Assertions.assertEquals(HttpUtils.APPLICATION_JSON, request.contentType());
        Assertions.assertEquals("https://postman-echo.com/get", request.uri());

        final var queryParams = request.queryParams();
        Assertions.assertEquals(2, queryParams.size());

        Assertions.assertEquals(1, queryParams.get("query1").size());
        Assertions.assertEquals("q1", queryParams.get("query1").get(0));

        Assertions.assertEquals(1, queryParams.get("query2").size());
        Assertions.assertEquals("q2", queryParams.get("query2").get(0));
    }

    @Test
    void queryParams_02() {
        factory.newInstance(RequestParamCase01.class).queryEncoded("1 + 1 = 2");

        Assertions.assertEquals("1 + 1 = 2", reqRef.get().queryParams().get("query 1").get(0), "Should not encode");
    }

    @Test
    void queryParams_03() {
        factory.newInstance(RequestParamCase01.class).getByMultiple("1 + 1 = 2", "3");

        final var queryParams = reqRef.get().queryParams();

        Assertions.assertEquals(1, queryParams.size());

        Assertions.assertEquals("1 + 1 = 2", queryParams.get("query 1").get(0));
        Assertions.assertEquals("3", queryParams.get("query 1").get(1));
    }

    @Test
    void queryParamList_01() {
        factory.newInstance(RequestParamCase01.class).getByList(List.of("1 + 1 = 2", "3"));

        final var queryParams = reqRef.get().queryParams();

        Assertions.assertEquals(1, queryParams.size());

        Assertions.assertEquals("1 + 1 = 2", queryParams.get("qList").get(0));
        Assertions.assertEquals("3", queryParams.get("qList").get(1));
    }

    @Test
    void queryParamMap_01() {
        final var newInstance = factory.newInstance(RequestParamCase01.class);

        newInstance.getByMap(Map.of("query 1", "1 + 1 = 2", "query2", "q2"));

        final var request = reqRef.get();

        Assertions.assertEquals(2, request.queryParams().size());
        Assertions.assertEquals("1 + 1 = 2", request.queryParams().get("query 1").get(0));
        Assertions.assertEquals("q2", request.queryParams().get("query2").get(0));
    }

    @Test
    void queryParamMap_02() {
        final var newInstance = factory.newInstance(RequestParamCase01.class);

        newInstance.getByMap(Map.of("query 1", "1 + 1 = 2", "query2", "q2-a"), "q2-b");

        final var request = reqRef.get();

        Assertions.assertEquals(2, request.queryParams().size());
        Assertions.assertEquals("1 + 1 = 2", request.queryParams().get("query 1").get(0));

        Assertions.assertEquals(2, request.queryParams().get("query2").size(), "Should collect all");
        Assertions.assertEquals("q2-a", request.queryParams().get("query2").get(0), "Should be determinstic in order");
        Assertions.assertEquals("q2-b", request.queryParams().get("query2").get(1));
    }

    @Test
    void acceptGzip_01() {
        factory.newInstance(HeaderTestCase01.class).get("1234");

        Assertions.assertTrue(reqRef.get().acceptEncoding().equalsIgnoreCase("gzip"), "should have the value");
    }

    @Test
    void acceptGzip_02() {
        factory.newInstance(HeaderTestCase01.AcceptGZipTestCase002.class).get();

        Assertions.assertTrue(reqRef.get().acceptEncoding() == null, "should have not the value");
    }

    @Test
    void header_01() {
        final var newInstance = factory.newInstance(HeaderTestCase01.class);

        newInstance.get("1234");

        Assertions.assertEquals("1234", reqRef.get().headers().get("x-correl-id").get(0), "should have the value");
    }

    @Test
    void header_02() {
        final var newInstance = factory.newInstance(HeaderTestCase01.class);

        newInstance.get("   ");

        Assertions.assertEquals("   ", reqRef.get().headers().get("x-correl-id").get(0));

        reqRef.set(null);

        newInstance.get((String) null);

        Assertions.assertEquals(0, reqRef.get().headers().size());
    }

    @Test
    void header_03() {
        final var newInstance = factory.newInstance(HeaderTestCase01.class);

        newInstance.get((String) null);

        Assertions.assertEquals(null, reqRef.get().headers().get("x-correl-id"));
    }

    @Test
    void header_04() {
        final var newInstance = factory.newInstance(HeaderTestCase01.class);

        newInstance.getBlank("1234");

        Assertions.assertEquals("1234", reqRef.get().headers().get("").get(0), "should take it as is");
    }

    @Test
    void header_05() {
        final var newInstance = factory.newInstance(HeaderTestCase01.class);

        final var uuid = UUID.randomUUID();

        newInstance.get(uuid);

        Assertions.assertEquals(uuid.toString(), reqRef.get().headers().get("x-uuid").get(0),
                "should have call toString");
    }

    @Test
    void header_06() {
        final var newInstance = factory.newInstance(HeaderTestCase01.class);

        newInstance.getRepeated("1", "2");

        final var headers = reqRef.get().headers().get("x-correl-id");

        Assertions.assertEquals(2, headers.size(), "should concate");
        Assertions.assertEquals("1", headers.get(0));
        Assertions.assertEquals("2", headers.get(1));
    }

    @Test
    void header_07() {
        final var newInstance = factory.newInstance(HeaderTestCase01.class);

        newInstance.getMultiple("1", "2");

        final var headers = reqRef.get().headers();

        Assertions.assertEquals(2, headers.size(), "should have both");
        Assertions.assertEquals("1", headers.get("x-span-id").get(0));
        Assertions.assertEquals("2", headers.get("x-trace-id").get(0));
    }

    @Test
    void header_08() {
        final var newInstance = factory.newInstance(HeaderTestCase01.class);

        newInstance.get(List.of("CN", "EN", "   "));

        final var headers = reqRef.get().headers().get("accept-language");

        Assertions.assertEquals(3, headers.size());
        Assertions.assertEquals("CN", headers.get(0));
        Assertions.assertEquals("EN", headers.get(1));
        Assertions.assertEquals("   ", headers.get(2));
    }

    @Test
    void header_09() {
        final var newInstance = factory.newInstance(HeaderTestCase01.class);

        newInstance.get(Map.of("CN", "EN", "   ", ""));

        final var headers = reqRef.get().headers();

        Assertions.assertEquals(2, headers.size(), "should have two headers");
        Assertions.assertEquals(1, headers.get("CN").size());
        Assertions.assertEquals(1, headers.get("   ").size());
    }

    @Test
    void header_10() {
        final var newInstance = factory.newInstance(HeaderTestCase01.class);

        newInstance.get(Map.of("x-correl-id", "mapped", "accept-language", "CN"), "uuid");

        final var headers = reqRef.get().headers();

        Assertions.assertEquals(true, headers.size() >= 2, "should have two headers at minimum");
        Assertions.assertEquals(2, headers.get("x-correl-id").size(), "should concate all values");
        Assertions.assertEquals(1, headers.get("accept-language").size());
    }

    @Test
    void header_11() {
        final var newInstance = factory.newInstance(HeaderTestCase01.class);

        newInstance.get(CollectionUtils
                .toMultiValueMap(Map.of("accept-language", List.of("CN", "EN"), "x-correl-id", List.of("uuid"))));

        final var headers = reqRef.get().headers();

        Assertions.assertEquals(true, headers.size() >= 2, "should have two headers");
        Assertions.assertEquals(1, headers.get("x-correl-id").size());
        Assertions.assertEquals(2, headers.get("accept-language").size());
    }

    @Test
    void header_12() {
        final var newInstance = factory.newInstance(HeaderTestCase01.class);

        newInstance.getMapOfList(Map.of("accept-language", List.of("CN", "EN"), "x-correl-id", List.of("uuid")));

        final var headers = reqRef.get().headers();

        Assertions.assertEquals(true, headers.size() >= 2);
        Assertions.assertEquals(1, headers.get("x-correl-id").size());
        Assertions.assertEquals(2, headers.get("accept-language").size());
    }

    @Test
    void header_13() {
        final var newInstance = factory.newInstance(HeaderTestCase01.class);

        newInstance.getListOfList(List.of(List.of("DE"), List.of("CN", "EN"), List.of("JP")));

        final var headers = reqRef.get().headers();

        Assertions.assertEquals(true, headers.size() >= 1);
        Assertions.assertEquals(4, headers.get("accept-language").size());
    }

    @Test
    void header_14() {
        final var newInstance = factory.newInstance(HeaderTestCase01.class);

        final var nullList = new ArrayList<String>();
        nullList.add("EN");
        nullList.add(null);
        nullList.add("CN");

        newInstance.getListOfList(List.of(List.of("DE"), nullList, List.of("JP")));

        final var headers = reqRef.get().headers();

        Assertions.assertTrue(headers.size() >= 1, "should filter out all nulls");
        Assertions.assertEquals(4, headers.get("accept-language").size());
    }

    @Test
    void header_15() {
        final var newInstance = factory.newInstance(HeaderTestCase01.class);

        newInstance.get(Map.of("x-correl-id", "mapped", "accept-language", "CN"), null);

        final var headers = reqRef.get().headers();

        Assertions.assertTrue(headers.size() >= 2, "should have two headers");
        Assertions.assertEquals(1, headers.get("x-correl-id").size(), "should filter out nulls");
        Assertions.assertEquals(1, headers.get("accept-language").size());
    }

    @Test
    void contentType_001() {
        final var newInstance = factory.newInstance(ContentTypeTestCases.Case001.class);

        newInstance.get1();

        var req = reqRef.get();

        Assertions.assertEquals("i-type", req.contentType());
        Assertions.assertEquals("i-accept", req.accept());
    }

    @Test
    void contentType_02() {
        final var newInstance = factory.newInstance(ContentTypeTestCases.Case001.class);

        newInstance.get2();

        final var req = reqRef.get();

        Assertions.assertEquals("i-type", req.contentType());
        Assertions.assertEquals(HttpUtils.APPLICATION_JSON, req.accept());
    }

    @Test
    void contentType_003() {
        final var newInstance = factory.newInstance(ContentTypeTestCases.Case001.class);

        newInstance.get3();

        var req = reqRef.get();

        Assertions.assertEquals("m-type", req.contentType());
        Assertions.assertEquals("m-accept", req.accept());
    }

    @Test
    void contentType_004() {
        final var newInstance = factory.newInstance(ContentTypeTestCases.Case002.class);

        newInstance.get1();

        var req = reqRef.get();

        Assertions.assertEquals("i-type", req.contentType());
        Assertions.assertEquals(HttpUtils.APPLICATION_JSON, req.accept());
    }

    @Test
    void contentType_05() {
        final var newInstance = factory.newInstance(ContentTypeTestCases.Case002.class);

        newInstance.get2();

        final var req = reqRef.get();

        Assertions.assertEquals("i-type", req.contentType());
        Assertions.assertEquals(HttpUtils.APPLICATION_JSON, req.accept());
    }

    @Test
    void contentType_06() {
        final var newInstance = factory.newInstance(ContentTypeTestCases.Case002.class);

        newInstance.get3();

        var req = reqRef.get();

        Assertions.assertEquals("m-type", req.contentType());
        Assertions.assertEquals("m-accept", req.accept());
    }

    @Test
    void exception_01() {
        final var checked = new IOException();
        final var restFnException = new RestFnException(checked);
        final var newInstance = new ByRestProxyFactory(config -> req -> {
            throw restFnException;
        }, clientConfig, parser).newInstance(ExceptionCase001.class);

        final var thrown = Assertions.assertThrows(RestFnException.class, newInstance::get);

        Assertions.assertEquals(restFnException, thrown);
    }

    @Test
    void exception_02() {
        final var checked = new IOException();
        final var restFnException = new RestFnException(checked);
        final var newInstance = new ByRestProxyFactory(config -> req -> {
            throw restFnException;
        }, clientConfig, parser).newInstance(ExceptionCase001.class);

        final var thrown = Assertions.assertThrows(IOException.class, newInstance::delete);

        Assertions.assertEquals(checked, thrown);
    }

    @Test
    void exception_03() {
        final var checked = new InterruptedException();
        final var restFnException = new RestFnException(checked);
        final var newInstance = new ByRestProxyFactory(config -> req -> {
            throw restFnException;
        }, clientConfig, parser)
                .newInstance(ExceptionCase001.class);

        final var thrown = Assertions.assertThrows(InterruptedException.class, newInstance::delete);

        Assertions.assertEquals(checked, thrown);
    }

    @Test
    void exception_04() {
        final var toBeThrown = new RuntimeException();
        final var newInstance = new ByRestProxyFactory(config -> req -> {
            throw toBeThrown;
        }, clientConfig, parser)
                .newInstance(ExceptionCase001.class);

        final var thrown = Assertions.assertThrows(RuntimeException.class, newInstance::delete);

        Assertions.assertEquals(toBeThrown, thrown);
    }

    @Test
    void exception_05() {
        Assertions.assertThrows(Exception.class,
                new ByRestProxyFactory(config -> req -> new MockHttpResponse<Instant>(200, Instant.now()), clientConfig,
                        parser)
                                .newInstance(ExceptionCase001.class)::post);
    }

    @Test
    void authInvocation_01() {
        final var authResolver = new MockInvocationAuthProviderResolver(
                new MockInvocationAuthProvider(UUID.randomUUID().toString()));

        final var parser = new DefaultProxyMethodParser(propertyResolver, authResolver, bodyHandlerResolver,
                bindingBodyHandlerProvider);

        final var newInstance = new ByRestProxyFactory(restFnProvider, clientConfig, parser)
                .newInstance(InvocationAuthCase01.class);

        newInstance.getOnInvocation();

        Assertions.assertEquals(authResolver.provider().header(), reqRef.get().authSupplier().get());
        Assertions.assertEquals("getOnInvocation", authResolver.takeName());

        final var invocation = authResolver.provider().takeInvocation();

        Assertions.assertEquals(InvocationAuthCase01.class, invocation.method().getDeclaringClass());
        Assertions.assertEquals(true, newInstance == invocation.target());
        Assertions.assertEquals(0, invocation.args().size());
    }

    @Test
    void authInvocation_02() {
        final var authResolver = new MockInvocationAuthProviderResolver(
                new MockInvocationAuthProvider(UUID.randomUUID().toString()));

        final var parser = new DefaultProxyMethodParser(propertyResolver, authResolver, bodyHandlerResolver,
                bindingBodyHandlerProvider);

        new ByRestProxyFactory(restFnProvider, clientConfig, parser)
                .newInstance(InvocationAuthCase01.class).get();

        Assertions.assertEquals(null, reqRef.get().authSupplier(), "should follow the interface with no Auth");
        Assertions.assertEquals(0, authResolver.count(), "should follow the interface with no Auth");
    }

    @Test
    void authInvocation_03() {
        final var authResolver = new MockInvocationAuthProviderResolver(
                new MockInvocationAuthProvider(UUID.randomUUID().toString()));

        final var parser = new DefaultProxyMethodParser(propertyResolver, authResolver, bodyHandlerResolver,
                bindingBodyHandlerProvider);

        new ByRestProxyFactory(restFnProvider, clientConfig, parser)
                .newInstance(InvocationAuthCase02.class).get();

        Assertions.assertEquals(authResolver.provider().header(), reqRef.get().authSupplier().get(),
                "should follow the interface with Auth");
        Assertions.assertEquals("getOnInterface", authResolver.takeName(), "should follow the interface with Auth");
    }

    @Test
    void authInvocation_04() {
        final var authResolver = new MockInvocationAuthProviderResolver(
                new MockInvocationAuthProvider(UUID.randomUUID().toString()));

        final var parser = new DefaultProxyMethodParser(propertyResolver, authResolver, bodyHandlerResolver,
                bindingBodyHandlerProvider);

        new ByRestProxyFactory(restFnProvider, clientConfig, parser)
                .newInstance(InvocationAuthCase02.class).getOnMethod();

        Assertions.assertEquals(authResolver.provider().header(), reqRef.get().authSupplier().get(),
                "should follow the interface");
        Assertions.assertEquals("getOnMethod", authResolver.takeName(), "should follow the method");
    }

    @Test
    void authInvocation_05() {
        final var authResolver = new MockInvocationAuthProviderResolver(
                new MockInvocationAuthProvider(UUID.randomUUID().toString()));

        final var parser = new DefaultProxyMethodParser(propertyResolver, authResolver, bodyHandlerResolver,
                bindingBodyHandlerProvider);

        new ByRestProxyFactory(restFnProvider, clientConfig, parser)
                .newInstance(InvocationAuthCase03.class).getOnMethod();

        Assertions.assertEquals(authResolver.provider().header(), reqRef.get().authSupplier().get(),
                "should follow the method");
        Assertions.assertEquals("getOnMethod", authResolver.takeName());
    }

    @Test
    void authInvocation_06() {
        final var authResolver = new MockInvocationAuthProviderResolver(
                new MockInvocationAuthProvider(UUID.randomUUID().toString()));

        final var parser = new DefaultProxyMethodParser(propertyResolver, authResolver, bodyHandlerResolver,
                bindingBodyHandlerProvider);

        new ByRestProxyFactory(restFnProvider, clientConfig, parser)
                .newInstance(InvocationAuthCase03.class).get();

        Assertions.assertEquals(null, reqRef.get().authSupplier().get(), "should follow the interface");
    }

    @Test
    void authSimple_01() {
        factory.newInstance(AuthTestCases.Case004.class).get();

        Assertions.assertEquals("CustomKey custom.header.123", reqRef.get().authSupplier().get());
    }

    @Test
    void authSimple_08() {
        factory.newInstance(SimpleAuthCase01.class).get();

        Assertions.assertEquals("SIMPLE", reqRef.get().authSupplier().get(), "should follow the interface");
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

        Assertions.assertEquals("Basic dXNlcjpuYW1l", reqRef.get().authSupplier().get());
    }

    @Test
    void authBasic_03() {
        factory.newInstance(AuthTestCases.Case002.class).get();

        Assertions.assertEquals("Basic cG9zdG1hbjpwYXNzd29yZA==", reqRef.get().authSupplier().get());
    }

    @Test
    void authBearer_01() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> factory.newInstance(BearerAuthCase01.class).get());
    }

    @Test
    void authBearer_02() {
        factory.newInstance(BearerAuthCase02.class).get();

        Assertions.assertEquals("Bearer token", reqRef.get().authSupplier().get());
    }

    @Test
    void authBearer_03() {
        factory.newInstance(AuthTestCases.Case003.class).get();

        Assertions.assertEquals("Bearer ec3fb099-7fa3-477b-82ce-05547babad95", reqRef.get().authSupplier().get());
    }

    @Test
    void authBean_09() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> factory.newInstance(BeanAuthCase05.class).get());
    }

    @Test
    void defaultAuth_01() {
        factory.newInstance(AuthTestCases.Case01.class).get();

        Assertions.assertEquals(null, reqRef.get().authSupplier(),
                "Should have no supplier leaving it to the global provider");
    }

    @Test
    void defaultAuth_02() {
        factory.newInstance(AuthTestCases.Case01.class).get("");

        Assertions.assertEquals("", reqRef.get().authSupplier().get());
    }

    @Test
    void defaultAuth_03() {
        factory.newInstance(AuthTestCases.Case01.class).get(" ");

        Assertions.assertEquals(" ", reqRef.get().authSupplier().get());
    }

    @Test
    void authHeader_01() {
        factory.newInstance(AuthTestCases.Case01.class).get(null);

        Assertions.assertEquals(null, reqRef.get().authSupplier().get());
    }

    @Test
    void authHeader_02() {
        factory.newInstance(AuthTestCases.Case003.class).get(null);

        Assertions.assertEquals(null, reqRef.get().authSupplier().get());
    }

    @Test
    void authHeader_03() {
        factory.newInstance(AuthTestCases.Case004.class).get("234");

        Assertions.assertEquals("234", reqRef.get().authSupplier().get());
    }

    @Test
    void authHeader_04() {
        factory.newInstance(AuthTestCases.Case005.class).get("");

        Assertions.assertEquals("", reqRef.get().authSupplier().get());
    }

    @Test
    void authHeader_05() {
        factory.newInstance(AuthTestCases.Case005.class).get("  ");

        Assertions.assertEquals("  ", reqRef.get().authSupplier().get());
    }

    @Test
    void authHeader_06() {
        factory.newInstance(AuthTestCases.Case005.class).get(null);

        Assertions.assertEquals(null, reqRef.get().authSupplier().get());
    }

    @Test
    void authHeader_07() {
        factory.newInstance(AuthTestCases.Case010.class).get("null");

        Assertions.assertEquals("null", reqRef.get().authSupplier().get());
    }

    @Test
    void authHeader_08() {
        factory.newInstance(AuthTestCases.Case010.class).get(null);

        Assertions.assertEquals(null, reqRef.get().authSupplier().get());
    }

    @Test
    void authNone_01() {
        factory.newInstance(AuthTestCases.Case010.class).get();

        Assertions.assertEquals(null, reqRef.get().authSupplier().get());
    }

    @Test
    void authException_01() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> factory.newInstance(AuthTestCases.Case007.class).get());
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> factory.newInstance(AuthTestCases.Case008.class).get());
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> factory.newInstance(AuthTestCases.Case009.class).get());
    }
}
