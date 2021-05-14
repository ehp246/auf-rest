package me.ehp246.aufrest.core.byrest;

import java.net.http.HttpResponse;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import me.ehp246.aufrest.api.rest.Invocation;
import me.ehp246.aufrest.api.rest.RestFn;

/**
 * @author Lei Yang
 *
 */
class ByRestFactoryInvokedOnTest {
    private final Invocation[] ref = new Invocation[] { null };

    private final RestFn client = request -> {
        ref[0] = request.invokedOn();
        return Mockito.mock(HttpResponse.class);
    };

    private final ByRestFactory factory = new ByRestFactory(cfg -> client, s -> s);

    @BeforeEach
    void beforeEach() {
        ref[0] = null;
    }

    @Test
    void test_001() {
        final var newInstance = factory.newInstance(InvokedOnTestCase001.class);

        newInstance.get();

        final var invokedOn = ref[0];

        Assertions.assertEquals(true, invokedOn.declaredType() == InvokedOnTestCase001.class);
        Assertions.assertEquals(true, invokedOn.target() == newInstance);
        Assertions.assertEquals(true, invokedOn.method().getName() == "get");
        Assertions.assertEquals(true, invokedOn.args().size() == 0);
    }

    @Test
    void test_002() throws NoSuchMethodException, SecurityException {
        final var m = InvokedOnTestCase001.class.getMethod("get", int.class);

        final var newInstance = factory.newInstance(InvokedOnTestCase001.class);

        newInstance.get(2);

        final var invokedOn = ref[0];

        Assertions.assertEquals(true, invokedOn.declaredType() == InvokedOnTestCase001.class);
        Assertions.assertEquals(true, invokedOn.target() == newInstance);
        Assertions.assertEquals(true, invokedOn.method().equals(m));
        Assertions.assertEquals(true, invokedOn.args().size() == 1);
        Assertions.assertEquals(Integer.class, invokedOn.args().get(0).getClass());
        Assertions.assertEquals(2, ((Integer) invokedOn.args().get(0)).intValue());
    }
}
