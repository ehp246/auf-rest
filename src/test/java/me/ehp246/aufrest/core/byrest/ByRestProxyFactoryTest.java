package me.ehp246.aufrest.core.byrest;

import java.io.IOException;
import java.net.http.HttpResponse;
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

import me.ehp246.aufrest.api.exception.RestFnException;
import me.ehp246.aufrest.api.rest.RestClientConfig;
import me.ehp246.aufrest.api.rest.RestFn;
import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.api.spi.Invocation;
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

/**
 * @author Lei Yang
 *
 */
class ByRestProxyFactoryTest {
    private final AtomicReference<RestRequest> reqRef = new AtomicReference<>();

    private final RestFn client = request -> {
        reqRef.set(request);
        return Mockito.mock(HttpResponse.class);
    };

    private final MockEnvironment env = new MockEnvironment().withProperty("echo.base", "https://postman-echo.com")
            .withProperty("api.bearer.token", "ec3fb099-7fa3-477b-82ce-05547babad95")
            .withProperty("postman.username", "postman").withProperty("postman.password", "password");

    private final ByRestProxyFactory factory = new ByRestProxyFactory(cfg -> client, env::resolveRequiredPlaceholders);

    @BeforeEach
    void beforeEach() {
        reqRef.set(null);
    }

    @Test
    void defaults_001() {
        final var newInstance = factory.newInstance(UriTestCase001.class);

        Assertions.assertEquals(true, newInstance.hashCode() == newInstance.hashCode());
        Assertions.assertEquals(true, newInstance.equals(List.of(newInstance).get(0)));
        Assertions.assertEquals(true, Set.of(newInstance).contains(newInstance));
        Assertions.assertEquals(true, newInstance instanceof UriTestCase001);
        Assertions.assertEquals(true, !newInstance.toString().isBlank());
        Assertions.assertEquals(1, newInstance.getInc(0));
    }

    @Test
    void header_001() {
        final var newInstance = factory.newInstance(RequestHeaderTestCase001.class);
        newInstance.get("1234");

        Assertions.assertEquals("1234", reqRef.get().headers().get("x-correl-id").get(0), "should have the value");

        reqRef.set(null);

        newInstance.get("	");

        Assertions.assertEquals("	", reqRef.get().headers().get("x-correl-id").get(0));

        reqRef.set(null);

        newInstance.get((String) null);

        Assertions.assertEquals(1, reqRef.get().headers().size());
    }

    @Test
    void header_002() {
        final var newInstance = factory.newInstance(RequestHeaderTestCase001.class);

        newInstance.getBlank("1234");

        Assertions.assertEquals(2, reqRef.get().headers().size());
    }

    @Test
    void header_003() {
        final var newInstance = factory.newInstance(RequestHeaderTestCase001.class);

        final var uuid = UUID.randomUUID();

        newInstance.get(uuid);

        Assertions.assertEquals(uuid.toString(), reqRef.get().headers().get("x-uuid").get(0),
                "should have call toString");
    }

    @Test
    void header_004() {
        final var newInstance = factory.newInstance(RequestHeaderTestCase001.class);

        newInstance.getRepeated("1", "2");

        final var headers = reqRef.get().headers();

        Assertions.assertEquals(2, headers.size());
        Assertions.assertEquals(2, headers.get("x-correl-id").size(), "should concate");
    }

    @Test
    void header_005() {
        final var newInstance = factory.newInstance(RequestHeaderTestCase001.class);

        newInstance.getMultiple("1", "2");

        final var headers = reqRef.get().headers();

        Assertions.assertEquals(3, headers.size(), "should have both");
        Assertions.assertEquals("1", headers.get("x-span-id").get(0));
        Assertions.assertEquals("2", headers.get("x-trace-id").get(0));
    }

    @Test
    void header_006() {
        final var newInstance = factory.newInstance(RequestHeaderTestCase001.class);

        newInstance.get(List.of("CN", "EN", "   "));

        final var headers = reqRef.get().headers().get("accept-language");

        Assertions.assertEquals(3, headers.size());
        Assertions.assertEquals("CN", headers.get(0));
        Assertions.assertEquals("EN", headers.get(1));
        Assertions.assertEquals("   ", headers.get(2));
    }

    @Test
    void header_007() {
        final var newInstance = factory.newInstance(RequestHeaderTestCase001.class);

        newInstance.get(Map.of("CN", "EN", "   ", ""));

        final var headers = reqRef.get().headers();

        Assertions.assertEquals(3, headers.size(), "should have two headers");
        Assertions.assertEquals(1, headers.get("CN").size());
        Assertions.assertEquals(1, headers.get("   ").size());
    }

    @Test
    void header_008() {
        final var newInstance = factory.newInstance(RequestHeaderTestCase001.class);

        newInstance.get(Map.of("x-correl-id", "mapped", "accept-language", "CN"), "uuid");

        final var headers = reqRef.get().headers();

        Assertions.assertEquals(true, headers.size() >= 2, "should have two headers at minimum");
        Assertions.assertEquals(2, headers.get("x-correl-id").size(), "should concate all values");
        Assertions.assertEquals(1, headers.get("accept-language").size());
    }

    @Test
    void header_009() {
        final var newInstance = factory.newInstance(RequestHeaderTestCase001.class);

        newInstance.get(CollectionUtils
                .toMultiValueMap(Map.of("accept-language", List.of("CN", "EN"), "x-correl-id", List.of("uuid"))));

        final var headers = reqRef.get().headers();

        Assertions.assertEquals(true, headers.size() >= 2, "should have two headers");
        Assertions.assertEquals(1, headers.get("x-correl-id").size());
        Assertions.assertEquals(2, headers.get("accept-language").size());
    }

    @Test
    void header_010() {
        final var newInstance = factory.newInstance(RequestHeaderTestCase001.class);

        newInstance.getMapOfList(Map.of("accept-language", List.of("CN", "EN"), "x-correl-id", List.of("uuid")));

        final var headers = reqRef.get().headers();

        Assertions.assertEquals(true, headers.size() >= 2);
        Assertions.assertEquals(1, headers.get("x-correl-id").size());
        Assertions.assertEquals(2, headers.get("accept-language").size());
    }

    @Test
    void header_011() {
        final var newInstance = factory.newInstance(RequestHeaderTestCase001.class);

        newInstance.getListOfList(List.of(List.of("DE"), List.of("CN", "EN"), List.of("JP")));

        final var headers = reqRef.get().headers();

        Assertions.assertEquals(true, headers.size() >= 1);
        Assertions.assertEquals(4, headers.get("accept-language").size());
    }

    @Test
    void header_012() {
        final var newInstance = factory.newInstance(RequestHeaderTestCase001.class);

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
    void header_013() {
        final var newInstance = factory.newInstance(RequestHeaderTestCase001.class);

        newInstance.get(Map.of("x-correl-id", "mapped", "accept-language", "CN"), null);

        final var headers = reqRef.get().headers();

        Assertions.assertTrue(headers.size() >= 2, "should have two headers");
        Assertions.assertEquals(1, headers.get("x-correl-id").size(), "should filter out nulls");
        Assertions.assertEquals(1, headers.get("accept-language").size());
    }

    @Test
    void exception_001() {
        final var checked = new IOException();
        final var restFnException = new RestFnException(checked);
        final var newInstance = new ByRestProxyFactory(config -> req -> {
            throw restFnException;
        }, s -> s).newInstance(ExceptionCase001.class);

        final var thrown = Assertions.assertThrows(RestFnException.class, newInstance::get);

        Assertions.assertEquals(restFnException, thrown);
    }

    @Test
    void exception_002() {
        final var checked = new IOException();
        final var restFnException = new RestFnException(checked);
        final var newInstance = new ByRestProxyFactory(config -> req -> {
            throw restFnException;
        }, s -> s).newInstance(ExceptionCase001.class);

        final var thrown = Assertions.assertThrows(IOException.class, newInstance::delete);

        Assertions.assertEquals(checked, thrown);
    }

    @Test
    void exception_003() {
        final var checked = new InterruptedException();
        final var restFnException = new RestFnException(checked);
        final var newInstance = new ByRestProxyFactory(config -> req -> {
            throw restFnException;
        }, s -> s).newInstance(ExceptionCase001.class);

        final var thrown = Assertions.assertThrows(InterruptedException.class, newInstance::delete);

        Assertions.assertEquals(checked, thrown);
    }

    @Test
    void exception_004() {
        final var toBeThrown = new RuntimeException();
        final var newInstance = new ByRestProxyFactory(config -> req -> {
            throw toBeThrown;
        }, s -> s).newInstance(ExceptionCase001.class);

        final var thrown = Assertions.assertThrows(RuntimeException.class, newInstance::delete);

        Assertions.assertEquals(toBeThrown, thrown);
    }

    @Test
    void exception_005() {
        Assertions.assertThrows(Exception.class,
                new ByRestProxyFactory(config -> req -> new MockHttpResponse<Instant>(200, Instant.now()))
                        .newInstance(ExceptionCase001.class)::post);
    }

    @Test
    void invocationAuth_01() {
        final var nameHolder = new String[1];
        final var invocationHolder = new Invocation[1];
        final var auth = UUID.randomUUID().toString();
        final var newInstance = new ByRestProxyFactory(cfg -> client, new RestClientConfig(),
                env::resolveRequiredPlaceholders, name -> {
                    nameHolder[0] = name;
                    return invocation -> {
                        invocationHolder[0] = invocation;
                        return auth;
                    };
                }).newInstance(InvocationAuthCase01.class);

        newInstance.getOnInvocation();

        // Auth supplier call is lazy.
        Assertions.assertEquals(auth, reqRef.get().authSupplier().get());
        Assertions.assertEquals("getOnInvocation", nameHolder[0]);
        final var invocation = invocationHolder[0];
        Assertions.assertEquals(InvocationAuthCase01.class, invocation.method().getDeclaringClass());
        Assertions.assertEquals(true, newInstance == invocation.target());
        Assertions.assertEquals(0, invocation.args().size());
    }

    @Test
    void invocationAuth_03() {
        final var nameHolder = new String[1];
        final var invocationHolder = new Invocation[1];
        final var auth = UUID.randomUUID().toString();

        new ByRestProxyFactory(cfg -> client, new RestClientConfig(), env::resolveRequiredPlaceholders, name -> {
            nameHolder[0] = name;
            return invocation -> {
                invocationHolder[0] = invocation;
                return auth;
            };
        }).newInstance(InvocationAuthCase02.class).get();

        Assertions.assertEquals(auth, reqRef.get().authSupplier().get(), "should follow the interface with Auth");
        Assertions.assertEquals("getOnInterface", nameHolder[0], "should follow the interface with Auth");
    }

    @Test
    void invocationAuth_04() {
        final var nameHolder = new String[1];
        final var invocationHolder = new Invocation[1];
        final var auth = UUID.randomUUID().toString();

        new ByRestProxyFactory(cfg -> client, new RestClientConfig(), env::resolveRequiredPlaceholders, name -> {
            nameHolder[0] = name;
            return invocation -> {
                invocationHolder[0] = invocation;
                return auth;
            };
        }).newInstance(InvocationAuthCase02.class).getOnMethod();

        Assertions.assertEquals(auth, reqRef.get().authSupplier().get(), "should follow the interface");
        Assertions.assertEquals("getOnMethod", nameHolder[0], "should follow the method");
    }

    @Test
    void invocationAuth_05() {
        final var nameHolder = new String[1];
        final var invocationHolder = new Invocation[1];
        final var auth = UUID.randomUUID().toString();

        new ByRestProxyFactory(cfg -> client, new RestClientConfig(), env::resolveRequiredPlaceholders, name -> {
            nameHolder[0] = name;
            return invocation -> {
                invocationHolder[0] = invocation;
                return auth;
            };
        }).newInstance(InvocationAuthCase03.class).getOnMethod();

        Assertions.assertEquals(auth, reqRef.get().authSupplier().get(), "should follow the method");
        Assertions.assertEquals("getOnMethod", nameHolder[0]);
    }

    @Test
    void invocationAuth_06() {
        new ByRestProxyFactory(cfg -> client, new RestClientConfig(), env::resolveRequiredPlaceholders,
                name -> invocation -> null).newInstance(InvocationAuthCase03.class).get();

        Assertions.assertEquals(null, reqRef.get().authSupplier().get(), "should follow the interface");
    }

    @Test
    void invocationAuth_07() {
        new ByRestProxyFactory(cfg -> client, new RestClientConfig(), env::resolveRequiredPlaceholders,
                name -> invocation -> null).newInstance(SimpleAuthCase01.class).get();

        Assertions.assertEquals("SIMPLE", reqRef.get().authSupplier().get(), "should follow the interface");
    }

    @Test
    void invocationAuth_08() {
        new ByRestProxyFactory(cfg -> client, new RestClientConfig(), env::resolveRequiredPlaceholders,
                name -> invocation -> null).newInstance(SimpleAuthCase01.class).get();

        Assertions.assertEquals("SIMPLE", reqRef.get().authSupplier().get(), "should follow the interface");
    }

    @Test
    void invocationAuth_09() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new ByRestProxyFactory(cfg -> client, new RestClientConfig(), env::resolveRequiredPlaceholders,
                        name -> invocation -> null).newInstance(BeanAuthCase05.class));
    }

    @Test
    void invocationAuth_10() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new ByRestProxyFactory(cfg -> client, new RestClientConfig(), env::resolveRequiredPlaceholders,
                        name -> invocation -> null).newInstance(SimpleAuthCase02.class));
    }

    @Test
    void basicAuth_01() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new ByRestProxyFactory(cfg -> client, new RestClientConfig(), env::resolveRequiredPlaceholders,
                        name -> invocation -> null).newInstance(BasicAuthCase01.class));
    }

    @Test
    void basicAuth_02() {
        new ByRestProxyFactory(cfg -> client, new RestClientConfig(), env::resolveRequiredPlaceholders,
                name -> invocation -> null).newInstance(BasicAuthCase02.class).get();
        
        Assertions.assertEquals("Basic dXNlcjpuYW1l", reqRef.get().authSupplier().get());
    }

    @Test
    void bearerAuth_01() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new ByRestProxyFactory(cfg -> client, new RestClientConfig(), env::resolveRequiredPlaceholders,
                        name -> invocation -> null).newInstance(BearerAuthCase01.class));
    }

    @Test
    void bearerAuth_02() {
        new ByRestProxyFactory(cfg -> client, new RestClientConfig(), env::resolveRequiredPlaceholders,
                name -> invocation -> null).newInstance(BearerAuthCase02.class).get();

        Assertions.assertEquals("Bearer token", reqRef.get().authSupplier().get());
    }
}
