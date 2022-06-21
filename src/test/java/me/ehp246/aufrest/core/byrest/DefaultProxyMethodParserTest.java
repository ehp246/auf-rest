package me.ehp246.aufrest.core.byrest;

import java.net.http.HttpResponse.BodyHandlers;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.env.MockEnvironment;

import me.ehp246.aufrest.api.rest.AuthBeanResolver;
import me.ehp246.aufrest.api.rest.BasicAuth;
import me.ehp246.aufrest.api.rest.BindingBodyHandlerProvider;
import me.ehp246.aufrest.api.spi.BodyHandlerResolver;
import me.ehp246.aufrest.api.spi.PropertyResolver;
import me.ehp246.aufrest.core.byrest.AuthTestCases.BeanAuth01;
import me.ehp246.aufrest.core.byrest.AuthTestCases.BeanAuth02;
import me.ehp246.aufrest.core.byrest.AuthTestCases.MockAuthBean;
import me.ehp246.aufrest.core.byrest.AuthTestCases.NoneAuth01;
import me.ehp246.aufrest.core.byrest.requestbody.BodyTestCases;
import me.ehp246.test.InvocationUtil;

/**
 * @author Lei Yang
 *
 */
class DefaultProxyMethodParserTest {
    private final BodyHandlerResolver bodyHandlerResolver = name -> BodyHandlers.discarding();
    private final BindingBodyHandlerProvider bindingBodyHandlerProvider = binding -> BodyHandlers.discarding();
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

        final var req = parser.parse(invocation.method()).apply(captor.proxy(), invocation.args());

        Assertions.assertEquals(null, req.body());
        Assertions.assertEquals(null, req.bodyAs());
    }

    @Test
    void body_02() {
        final var captor = InvocationUtil.newCaptor(BodyTestCases.RequestCase01.class);
        final var expected = UUID.randomUUID().toString();
        captor.proxy().getWithAuthParam("", expected);

        final var invocation = captor.invocation();

        final var req = parser.parse(invocation.method()).apply(captor.proxy(), invocation.args());

        Assertions.assertEquals(expected, req.body());
        Assertions.assertEquals(String.class, req.bodyAs().type());
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

        final var authSupplier = parser.parse(invocation.method()).apply(captor.proxy(), invocation.args())
                .authSupplier();

        final var expected = new BasicAuth("username", "password").value();

        Assertions.assertEquals(expected, authSupplier.get());
        Assertions.assertEquals(expected, authSupplier.get());
        Assertions.assertEquals(expected, authSupplier.get());
        Assertions.assertEquals(1, authBean.takeCount());
    }

    @Test
    void authBean_02() {
        final var expected = "username";
        final var captor = InvocationUtil.newCaptor(BeanAuth01.class);

        captor.proxy().get(expected);

        final var invocation = captor.invocation();

        final var authSupplier = new DefaultProxyMethodParser(propertyResolver, name -> name, bodyHandlerResolver,
                bindingBodyHandlerProvider).parse(invocation.method()).apply(captor.proxy(), invocation.args())
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

        final var captor = InvocationUtil.newCaptor(BeanAuth01.class);
        captor.proxy().get();
        final var invocation = captor.invocation();

        Assertions.assertThrows(IllegalArgumentException.class, () -> parser.parse(invocation.method()))
                .printStackTrace();
        ;
    }

    @Test
    void authBean_09() {
        final var parser = new DefaultProxyMethodParser(propertyResolver, name -> new Object(), bodyHandlerResolver,
                bindingBodyHandlerProvider);

        final var captor = InvocationUtil.newCaptor(BeanAuth02.class);
        captor.proxy().get();

        Assertions.assertThrows(IllegalArgumentException.class, () -> parser.parse(captor.invocation().method()))
                .printStackTrace();
    }

    @Test
    void authNone_01() {
        final var captor = InvocationUtil.newCaptor(NoneAuth01.class);
        captor.proxy().get();

        final var invocation = captor.invocation();

        final var req = parser.parse(invocation.method()).apply(captor.proxy(), invocation.args());

        Assertions.assertEquals(null, req.authSupplier().get(), "should follow the interface");
    }

}
