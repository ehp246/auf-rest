package me.ehp246.test.mock;

import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodySubscriber;
import java.net.http.HttpResponse.BodySubscribers;
import java.net.http.HttpResponse.ResponseInfo;
import java.nio.charset.StandardCharsets;

import me.ehp246.aufrest.api.rest.InferringBodyHandlerProvider;
import me.ehp246.aufrest.api.rest.BodyHandlerType;

/**
 * @author Lei Yang
 *
 */
public class MockBodyHandler<T> implements HttpResponse.BodyHandler<T> {
    private final T body;
    private String asReturned;

    public MockBodyHandler(final T body) {
        super();
        this.body = body;
    }

    @Override
    public BodySubscriber<T> apply(final ResponseInfo responseInfo) {
        return BodySubscribers.mapping(BodySubscribers.ofString(StandardCharsets.UTF_8), s -> {
            asReturned = s;
            return body;
        });
    }

    public String asReturned() {
        return asReturned;
    }

    public InferringBodyHandlerProvider toProvider() {
        return new InferringBodyHandlerProvider() {
            @SuppressWarnings("unchecked")
            @Override
            public <T> BodyHandler<T> get(final BodyHandlerType<T> descriptor) {
                return (BodyHandler<T>) MockBodyHandler.this;
            }
        };
    }
}
