package me.ehp246.aufrest.provider.httpclient;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.function.Supplier;

import org.mockito.Mockito;

import me.ehp246.aufrest.mock.MockHttpResponse;

/**
 * @author Lei Yang
 *
 */
class MockClientBuilderSupplier {
    private int builderCount = 0;
    private final Supplier<HttpResponse<?>> responseSupplier;
    private HttpRequest sent = null;
    private final Exception e;

    MockClientBuilderSupplier() {
        super();
        this.responseSupplier = null;
        this.e = null;
    }

    MockClientBuilderSupplier(Exception e) {
        super();
        this.responseSupplier = null;
        this.e = e;
    }

    MockClientBuilderSupplier(final Supplier<HttpResponse<?>> responseSupplier) {
        super();
        this.responseSupplier = responseSupplier;
        this.e = null;
    }

    @SuppressWarnings("unchecked")
    HttpClient.Builder builder() {
        builderCount++;
        sent = null;

        final HttpClient client = Mockito.mock(HttpClient.class);

        try {
            if (this.e == null) {
                Mockito.when(client.send(Mockito.any(), Mockito.any())).thenAnswer(invocation -> {
                    sent = invocation.getArgument(0);
                    return (HttpResponse<Object>) Optional.ofNullable(responseSupplier).map(Supplier::get)
                            .orElseGet(MockHttpResponse::new);
                });
            } else {
                Mockito.when(client.send(Mockito.any(), Mockito.any())).thenThrow(e);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException();
        }

        final var builder = Mockito.mock(HttpClient.Builder.class);

        Mockito.when(builder.build()).thenReturn(client);

        return builder;
    }

    static HttpClient.Builder builder(HttpResponse<?> httpResponse) {
        final HttpClient client = Mockito.mock(HttpClient.class);

        try {
            Mockito.when(client.send(Mockito.any(), Mockito.any())).thenAnswer(invocation -> {
                return httpResponse;
            });
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException();
        }

        final var builder = Mockito.mock(HttpClient.Builder.class);

        Mockito.when(builder.build()).thenReturn(client);

        return builder;
    }

    static Supplier<HttpClient.Builder> supplier(HttpResponse<?> httpResponse) {
        return () -> builder(httpResponse);
    }

    int builderCount() {
        return builderCount;
    }

    HttpRequest requestSent() {
        return sent;
    }
}
