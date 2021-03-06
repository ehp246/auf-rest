package me.ehp246.aufrest.core.byrest.returntype;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.mock.env.MockEnvironment;

import me.ehp246.aufrest.api.rest.RestClientConfig;
import me.ehp246.aufrest.api.rest.RestFn;
import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.core.byrest.ByRestFactory;

/**
 * @author Lei Yang
 *
 */
class ReturnTypeTest {
    final RestClientConfig clientConfig = new RestClientConfig() {
    };
    private final AtomicReference<RestRequest> reqRef = new AtomicReference<>();
    private final RestFn client = request -> {
        reqRef.set(request);
        return Mockito.mock(HttpResponse.class);
    };
    private final ByRestFactory factory = new ByRestFactory(cfg -> client,
            new MockEnvironment()::resolveRequiredPlaceholders);

    private final ReturnTypeTestCase001 case001 = factory.newInstance(ReturnTypeTestCase001.class);

    @BeforeEach
    void beforeEach() {
        reqRef.set(null);
    }

    @Test
    void return_type_001() {
        Assertions.assertThrows(IllegalArgumentException.class,
                factory.newInstance(ReturnTypeTestCase001.class)::get001);
    }

    @Test
    void return_type_002() {
        Assertions.assertThrows(IllegalArgumentException.class,
                factory.newInstance(ReturnTypeTestCase001.class)::get002);
    }

    @Test
    void return_type_003() {
        Assertions.assertThrows(Exception.class, factory.newInstance(ReturnTypeTestCase001.class)::get004);
    }

    @Test
    void return_type_004() {
        Assertions.assertThrows(Exception.class, factory.newInstance(ReturnTypeTestCase001.class)::get005);
    }

    @Disabled
    @Test
    void receiver_001() throws Exception {
        case001.get003().get();

        final var bodyReceiver = reqRef.get().bodyReceiver();

        Assertions.assertEquals(List.class, bodyReceiver.type());

        Assertions.assertEquals(Instant.class, bodyReceiver.reifying().get(0));
    }

    @Disabled
    @Test
    void receiver_002() throws Exception {
        final var typeRef = new ParameterizedTypeReference<List<Instant>>() {
        };

        final var t = (ParameterizedType) typeRef.getClass().getGenericSuperclass();
        final Type[] actualTypeArguments = t.getActualTypeArguments();

        case001.get006(typeRef);

        final var bodyReceiver = reqRef.get().bodyReceiver();

        Assertions.assertEquals(List.class, bodyReceiver.type());

        Assertions.assertEquals(Instant.class, bodyReceiver.reifying().get(0));
    }

}
