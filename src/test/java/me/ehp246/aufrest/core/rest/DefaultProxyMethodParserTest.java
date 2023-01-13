package me.ehp246.aufrest.core.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.util.CollectionUtils;

import me.ehp246.aufrest.api.rest.AuthBeanResolver;
import me.ehp246.aufrest.api.rest.BasicAuth;
import me.ehp246.aufrest.api.rest.BodyHandlerBeanResolver;
import me.ehp246.aufrest.api.rest.InferringBodyHandlerProvider;
import me.ehp246.aufrest.api.spi.PropertyResolver;
import me.ehp246.aufrest.core.rest.AuthTestCases.BeanAuth01;
import me.ehp246.aufrest.core.rest.AuthTestCases.BeanAuth02;
import me.ehp246.aufrest.core.rest.AuthTestCases.BeanAuth03;
import me.ehp246.aufrest.core.rest.AuthTestCases.BeanAuth04;
import me.ehp246.aufrest.core.rest.AuthTestCases.BeanAuth05;
import me.ehp246.aufrest.core.rest.AuthTestCases.MockAuthBean;
import me.ehp246.aufrest.core.rest.AuthTestCases.NoneAuth01;
import me.ehp246.aufrest.core.rest.requestbody.BodyTestCases;
import me.ehp246.test.Invocation;
import me.ehp246.test.InvocationUtil;
import me.ehp246.test.mock.MockBodyHandlerProvider;

/**
 * @author Lei Yang
 *
 */
class DefaultProxyMethodParserTest {
    private final BodyHandlerBeanResolver bodyHandlerResolver = name -> r -> null;
    private final InferringBodyHandlerProvider bindingBodyHandlerProvider = new MockBodyHandlerProvider();
    private final PropertyResolver propertyResolver = new MockEnvironment()::resolveRequiredPlaceholders;
    private final MockAuthBean authBean = new MockAuthBean();
    private final AuthBeanResolver beanResolver = name -> {
        if (!name.equals("getOnInterface")) {
            throw new RuntimeException(name);
        }
        return authBean;
    };
    private final DefaultProxyMethodParser parser = new DefaultProxyMethodParser(propertyResolver, beanResolver,
            bodyHandlerResolver, bindingBodyHandlerProvider);

    @Test
    void body_01() {
        final var captor = InvocationUtil.newCaptor(BodyTestCases.RequestCase01.class);

        captor.proxy().getWithAuthParam("");

        final var invocation = captor.invocation();

        final var req = parser.parse(invocation.method()).apply(captor.proxy(), invocation.args()).request();

        Assertions.assertEquals(null, req.body());
    }

    @Test
    void body_02() {
        final var captor = InvocationUtil.newCaptor(BodyTestCases.RequestCase01.class);
        final var expected = UUID.randomUUID().toString();
        captor.proxy().getWithAuthParam("", expected);

        final var invocation = captor.invocation();

        final var req = parser.parse(invocation.method()).apply(captor.proxy(), invocation.args()).request();

        Assertions.assertEquals(expected, req.body());
    }

    @Test
    void authBean_01() {
        final var authBean = new MockAuthBean();
        final var authResolver = Mockito.mock(AuthBeanResolver.class);
        Mockito.when(authResolver.get(Mockito.eq("getOnInterface"))).thenReturn(authBean);

        final var parser = new DefaultProxyMethodParser(propertyResolver, authResolver, bodyHandlerResolver,
                bindingBodyHandlerProvider);

        final var captor = InvocationUtil.newCaptor(BeanAuth01.class);
        captor.proxy().getOnArgs("username", "password");
        final var invocation = captor.invocation();

        final var authSupplier = parser.parse(invocation.method()).apply(captor.proxy(), invocation.args()).request()
                .authSupplier();

        final var expected = new BasicAuth("username", "password").header();

        Assertions.assertEquals(expected, authSupplier.get());
        Assertions.assertEquals(expected, authSupplier.get());
        Assertions.assertEquals(expected, authSupplier.get());
        Assertions.assertEquals(1, authBean.takeBasicCount());
    }

    @Test
    void authBean_02() {
        final var expected = "username";
        final var captor = InvocationUtil.newCaptor(BeanAuth01.class);

        captor.proxy().get(expected);

        final var invocation = captor.invocation();

        final var authSupplier = new DefaultProxyMethodParser(propertyResolver, name -> name, bodyHandlerResolver,
                bindingBodyHandlerProvider).parse(invocation.method()).apply(captor.proxy(), invocation.args())
                        .request()
                        .authSupplier();

        Assertions.assertEquals(expected, authSupplier.get(), "should have the AuthHeader value");
    }

    @Test
    void authBean_03() {
        final var expected = (String) null;
        final var captor = InvocationUtil.newCaptor(BeanAuth01.class);

        captor.proxy().get(expected);

        final var invocation = captor.invocation();

        final var authSupplier = new DefaultProxyMethodParser(propertyResolver, name -> name, bodyHandlerResolver,
                bindingBodyHandlerProvider).parse(invocation.method()).apply(captor.proxy(), invocation.args())
                        .request()
                        .authSupplier();

        Assertions.assertEquals(expected, authSupplier.get(), "should have the AuthHeader value");
    }

    @Test
    void authBean_04() {
        final var authBean = new MockAuthBean();
        final var authResolver = Mockito.mock(AuthBeanResolver.class);
        Mockito.when(authResolver.get(Mockito.eq("getOnInterface"))).thenReturn(authBean);

        final var parser = new DefaultProxyMethodParser(propertyResolver, authResolver, bodyHandlerResolver,
                bindingBodyHandlerProvider);

        final var captRef = new Invocation[1];
        InvocationUtil.newInvocation(BeanAuth01.class, captRef).get();

        Assertions
                .assertThrows(IllegalArgumentException.class,
                        () -> parser.parse(captRef[0].method()).apply(captRef[0].target(), captRef[0].args()));
    }

    @Test
    void authBean_method_01() {
        final var authBean = new MockAuthBean();
        final var resolver = Mockito.mock(AuthBeanResolver.class);
        Mockito.when(resolver.get(Mockito.eq("getOnInterface"))).thenReturn(authBean);

        final var parser = new DefaultProxyMethodParser(propertyResolver, resolver, bodyHandlerResolver,
                bindingBodyHandlerProvider);

        final var captRef = new Invocation[1];
        InvocationUtil.newInvocation(BeanAuth03.class, captRef).get(UUID.randomUUID().toString());

        parser.parse(captRef[0].method()).apply(captRef[0].target(), captRef[0].args());

        Assertions.assertEquals(1, authBean.takeBearerTokenCount());
    }

    @Test
    void authBean_method_02() {
        final var authBean = new MockAuthBean();
        final var resolver = Mockito.mock(AuthBeanResolver.class);
        Mockito.when(resolver.get(Mockito.eq("getOnInterface"))).thenReturn(authBean);

        final var parser = new DefaultProxyMethodParser(propertyResolver, resolver, bodyHandlerResolver,
                bindingBodyHandlerProvider);

        final var captRef = new Invocation[1];
        InvocationUtil.newInvocation(BeanAuth04.class, captRef).get();

        parser.parse(captRef[0].method()).apply(captRef[0].target(), captRef[0].args());

        Assertions.assertEquals(1, authBean.takeRandomCount());
    }

    @Test
    void authBean_08() {
        final var authBean = new BasicAuth(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        final var resolver = Mockito.mock(AuthBeanResolver.class);
        Mockito.when(resolver.get(Mockito.eq("getOnInterface"))).thenReturn(authBean);

        final var parser = new DefaultProxyMethodParser(propertyResolver, resolver, bodyHandlerResolver,
                bindingBodyHandlerProvider);

        final var captRef = new Invocation[1];
        InvocationUtil.newInvocation(BeanAuth05.class, captRef).get();

        final var req = parser.parse(captRef[0].method()).apply(captRef[0].target(), captRef[0].args()).request();

        Assertions.assertEquals(authBean.header(), req.authSupplier().get(),
                "should call the un-annotated named method");
    }

    @Test
    void authBean_09() {
        final var parser = new DefaultProxyMethodParser(propertyResolver, name -> new Object(), bodyHandlerResolver,
                bindingBodyHandlerProvider);

        final var captor = InvocationUtil.newCaptor(BeanAuth02.class);
        captor.proxy().get();

        Assertions.assertThrows(IllegalArgumentException.class, () -> parser.parse(captor.invocation().method()));
    }

    @Test
    void authNone_01() {
        final var captor = InvocationUtil.newCaptor(NoneAuth01.class);
        captor.proxy().get();

        final var invocation = captor.invocation();

        final var req = parser.parse(invocation.method()).apply(captor.proxy(), invocation.args()).request();

        Assertions.assertEquals(null, req.authSupplier().get(), "should follow the interface");
    }

    @Test
    void header_01() {
        final var captor = InvocationUtil.newCaptor(HeaderTestCases.HeaderCase01.class);
        captor.proxy().getRepeated("", "");

        final var invocation = captor.invocation();

        Assertions
                .assertThrows(IllegalArgumentException.class,
                        () -> parser.parse(invocation.method()).apply(captor.proxy(), invocation.args()))
                ;
    }

    @Test
    void header_02() {
        final var captor = InvocationUtil.newCaptor(HeaderTestCases.HeaderCase01.class);

        captor.proxy().get(List.of("CN", "EN", "   "));

        final var invocation = captor.invocation();

        final var header = parser.parse(invocation.method()).apply(captor.proxy(), invocation.args()).request()
                .headers()
                .get("accept-language");

        Assertions.assertEquals(3, header.size());
        Assertions.assertEquals("CN", header.get(0));
        Assertions.assertEquals("EN", header.get(1));
        Assertions.assertEquals("   ", header.get(2));
    }

    @Test
    void header_03() {
        final var captor = InvocationUtil.newCaptor(HeaderTestCases.HeaderCase01.class);

        captor.proxy().get(Map.of("x-correl-id", "mapped", "accept-language", "CN"), "uuid");

        final var invocation = captor.invocation();

        final var headers = parser.parse(invocation.method()).apply(captor.proxy(), invocation.args()).request()
                .headers();

        Assertions.assertEquals(true, headers.size() >= 2, "should have two headers at minimum");
        Assertions.assertEquals(2, headers.get("x-correl-id").size(), "should collect every value");
        Assertions.assertEquals(true, headers.get("x-correl-id").contains("mapped"));
        Assertions.assertEquals(true, headers.get("x-correl-id").contains("uuid"));
        Assertions.assertEquals(1, headers.get("accept-language").size());
    }

    @Test
    void header_04() {
        final var captor = InvocationUtil.newCaptor(HeaderTestCases.HeaderCase01.class);

        captor.proxy().get(CollectionUtils
                .toMultiValueMap(Map.of("accept-language", List.of("CN", "EN"), "x-correl-id", List.of("uuid"))));

        final var invocation = captor.invocation();

        final var headers = parser.parse(invocation.method()).apply(captor.proxy(), invocation.args()).request()
                .headers();

        Assertions.assertEquals(true, headers.size() >= 2, "should have two headers");
        Assertions.assertEquals(1, headers.get("x-correl-id").size());
        Assertions.assertEquals(2, headers.get("accept-language").size());
    }

    @Test
    void header_05() {
        final var captor = InvocationUtil.newCaptor(HeaderTestCases.HeaderCase01.class);

        captor.proxy().getMapOfList(Map.of("accept-language", List.of("CN", "EN"), "x-correl-id", List.of("uuid")));

        final var invocation = captor.invocation();

        final var headers = parser.parse(invocation.method()).apply(captor.proxy(), invocation.args()).request()
                .headers();

        Assertions.assertEquals(true, headers.size() >= 2);
        Assertions.assertEquals(1, headers.get("x-correl-id").size());
        Assertions.assertEquals(2, headers.get("accept-language").size());
    }

    @Test
    void header_06() {
        final var captor = InvocationUtil.newCaptor(HeaderTestCases.HeaderCase01.class);

        captor.proxy().getListOfList(List.of(List.of("DE"), List.of("CN", "EN"), List.of("JP")));

        final var invocation = captor.invocation();

        final var headers = parser.parse(invocation.method()).apply(captor.proxy(), invocation.args()).request()
                .headers();

        Assertions.assertEquals(true, headers.size() >= 1);
        Assertions.assertEquals(4, headers.get("accept-language").size());
    }

    @Test
    void header_07() {
        final var captor = InvocationUtil.newCaptor(HeaderTestCases.HeaderCase01.class);

        final var nullList = new ArrayList<String>();
        nullList.add("EN");
        nullList.add(null);
        nullList.add("CN");

        captor.proxy().getListOfList(List.of(List.of("DE"), nullList, List.of("JP")));

        final var invocation = captor.invocation();

        final var headers = parser.parse(invocation.method()).apply(captor.proxy(), invocation.args()).request()
                .headers();

        Assertions.assertTrue(headers.size() >= 1, "should filter out all nulls");
        Assertions.assertEquals(4, headers.get("accept-language").size());
    }

    @Test
    void header_08() {
        final var captor = InvocationUtil.newCaptor(HeaderTestCases.HeaderCase01.class);

        captor.proxy().get(Map.of("x-correl-id", "id from map", "accept-language", "CN"), null);

        final var invocation = captor.invocation();

        final var headers = parser.parse(invocation.method()).apply(captor.proxy(), invocation.args()).request()
                .headers();

        Assertions.assertEquals("id from map", headers.get("x-correl-id").get(0));
        Assertions.assertEquals(1, headers.get("accept-language").size());
    }

    @Test
    void header_09() {
        final var captor = InvocationUtil.newCaptor(HeaderTestCases.HeaderCase01.class);

        captor.proxy().get(Map.of("CN", "EN", "   ", ""));

        final var invocation = captor.invocation();

        final var headers = parser.parse(invocation.method()).apply(captor.proxy(), invocation.args()).request()
                .headers();

        Assertions.assertEquals(2, headers.size(), "should have two headers");
        Assertions.assertEquals(1, headers.get("cn").size());
        Assertions.assertEquals(1, headers.get("   ").size());
    }

    @Test
    void headers_byRest_01() {
        final var captor = InvocationUtil.newCaptor(HeaderTestCases.HeaderCase02.class);
        captor.proxy().get();

        final var invocation = captor.invocation();

        final var apiKey = parser.parse(invocation.method()).apply(captor.proxy(), invocation.args()).request()
                .headers()
                .get("x-api-key");

        Assertions.assertEquals(1, apiKey.size());
        Assertions.assertEquals("api.key", apiKey.get(0));
    }

    @Test
    void headers_case02_01() {
        final var captor = InvocationUtil.newCaptor(HeaderTestCases.HeaderCase02.class);
        final var expected = new String[] { UUID.randomUUID().toString() };
        captor.proxy().get(expected[0]);

        final var invocation = captor.invocation();

        final var apiKey = parser.parse(invocation.method()).apply(captor.proxy(), invocation.args()).request()
                .headers()
                .get("x-api-key");

        // No order is defined.
        Assertions.assertEquals(1, apiKey.size());
        Assertions.assertEquals(true, apiKey.contains(expected[0]));
    }

    @Test
    void headers_case02_02() {
        final var captor = InvocationUtil.newCaptor(HeaderTestCases.HeaderCase02.class);

        captor.proxy().get(null);

        final var invocation = captor.invocation();

        Assertions.assertEquals(null,
                parser.parse(invocation.method()).apply(captor.proxy(), invocation.args()).request().headers()
                        .get("x-api-key"));
    }

    @Test
    void headers_case02_03() {
        final var captor = InvocationUtil.newCaptor(HeaderTestCases.HeaderCase02.class);

        captor.proxy().getCasing(null);

        final var invocation = captor.invocation();

        final var apiKey = parser.parse(invocation.method()).apply(captor.proxy(), invocation.args()).request()
                .headers()
                .get("x-api-key");

        Assertions.assertEquals(null, apiKey);
    }

    @Test
    void headers_case02_04() {
        final var captor = InvocationUtil.newCaptor(HeaderTestCases.HeaderCase02.class);

        final var expected = UUID.randomUUID().toString();
        captor.proxy().getCasing(expected);

        final var invocation = captor.invocation();

        final var apiKey = parser.parse(invocation.method()).apply(captor.proxy(), invocation.args()).request()
                .headers()
                .get("x-api-key");

        Assertions.assertEquals(1, apiKey.size());
        Assertions.assertEquals(expected, apiKey.get(0));
    }

    @Test
    void headers_byRest_03() {
        final var captor = InvocationUtil.newCaptor(HeaderTestCases.HeaderCase03.class);
        captor.proxy().get();

        final var invocation = captor.invocation();

        Assertions
                .assertThrows(IllegalArgumentException.class,
                        () -> parser.parse(invocation.method()).apply(captor.proxy(), invocation.args()))
                ;
    }

    @Test
    void headers_byRest_04() {
        final var captor = InvocationUtil.newCaptor(HeaderTestCases.HeaderCase04.class);
        captor.proxy().get();

        final var invocation = captor.invocation();

        final var headers = parser.parse(invocation.method()).apply(captor.proxy(), invocation.args()).request()
                .headers();

        Assertions.assertEquals(1, headers.get("x-api-key-1").size());
        Assertions.assertEquals("api.key.1", headers.get("x-api-key-1").get(0));

        Assertions.assertEquals(1, headers.get("x-api-key-2").size());
        Assertions.assertEquals("api.key.2", headers.get("x-api-key-2").get(0));
    }

    @Test
    void headers_byRest_05() {
        final var captor = InvocationUtil.newCaptor(HeaderTestCases.HeaderCase05.class);
        captor.proxy().get();

        final var invocation = captor.invocation();

        Assertions
                .assertThrows(IllegalArgumentException.class,
                        () -> parser.parse(invocation.method()).apply(captor.proxy(), invocation.args()))
                ;
    }

    @Test
    void headers_byRest_06() {
        final var captor = InvocationUtil.newCaptor(HeaderTestCases.HeaderCase06.class);
        captor.proxy().get();

        final var invocation = captor.invocation();

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> parser.parse(invocation.method()).apply(captor.proxy(), invocation.args()),
                "should not allow duplicate names");
    }

    @Test
    void headers_byRest_07() {
        final var captor = InvocationUtil.newCaptor(HeaderTestCases.HeaderCase07.class);
        captor.proxy().get();

        final var invocation = captor.invocation();

        Assertions
                .assertThrows(IllegalArgumentException.class,
                        () -> parser.parse(invocation.method()).apply(captor.proxy(), invocation.args()))
                ;
    }

    @Test
    void uri_01() {

    }
}
