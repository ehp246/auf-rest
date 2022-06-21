package me.ehp246.aufrest.core.byrest;

import java.net.http.HttpResponse.BodyHandlers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.env.MockEnvironment;

import me.ehp246.aufrest.api.rest.BasicAuth;
import me.ehp246.aufrest.api.rest.BindingBodyHandlerProvider;
import me.ehp246.aufrest.api.spi.BodyHandlerResolver;
import me.ehp246.aufrest.api.spi.InvocationAuthProviderResolver;
import me.ehp246.aufrest.api.spi.PropertyResolver;
import me.ehp246.aufrest.core.byrest.AuthTestCases.InvocationAuthCase02;
import me.ehp246.aufrest.core.byrest.AuthTestCases.MockAuthBean;
import me.ehp246.test.InvocationUtil;

/**
 * @author Lei Yang
 *
 */
class DefaultProxyMethodParserTest {
    private final BodyHandlerResolver bodyHandlerResolver = name -> BodyHandlers.discarding();
    private final BindingBodyHandlerProvider bindingBodyHandlerProvider = binding -> BodyHandlers.discarding();
    private final PropertyResolver propertyResolver = new MockEnvironment()::resolveRequiredPlaceholders;

    @Test
    void authInvocation_01() {
        final var authBean = new MockAuthBean();
        final var authResolver = Mockito.mock(InvocationAuthProviderResolver.class);
        Mockito.when(authResolver.get(Mockito.eq("getOnInterface"))).thenReturn(authBean);

        final var parser = new DefaultProxyMethodParser(propertyResolver, authResolver, bodyHandlerResolver,
                bindingBodyHandlerProvider);

        final var captor = InvocationUtil.newCaptor(InvocationAuthCase02.class);
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
    void authInvocation_03() {
        final var authBean = new MockAuthBean();
        final var authResolver = Mockito.mock(InvocationAuthProviderResolver.class);
        Mockito.when(authResolver.get(Mockito.eq("getOnInterface"))).thenReturn(authBean);

        final var parser = new DefaultProxyMethodParser(propertyResolver, authResolver, bodyHandlerResolver,
                bindingBodyHandlerProvider);

        final var captor = InvocationUtil.newCaptor(InvocationAuthCase02.class);
        captor.proxy().get();
        final var invocation = captor.invocation();

        Assertions.assertThrows(IllegalArgumentException.class, () -> parser.parse(invocation.method()))
                .printStackTrace();
        ;
    }

}
