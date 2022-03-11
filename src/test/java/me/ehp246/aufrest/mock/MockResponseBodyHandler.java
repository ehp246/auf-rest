package me.ehp246.aufrest.mock;

import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodySubscriber;
import java.net.http.HttpResponse.BodySubscribers;
import java.net.http.HttpResponse.ResponseInfo;
import java.nio.charset.StandardCharsets;

/**
 * @author Lei Yang
 *
 */
public class MockResponseBodyHandler<T> implements HttpResponse.BodyHandler<T> {
    private final T body;
    private String asReturned;

    public MockResponseBodyHandler(T body) {
        super();
        this.body = body;
    }

    @Override
    public BodySubscriber<T> apply(ResponseInfo responseInfo) {
        return BodySubscribers.mapping(BodySubscribers.ofString(StandardCharsets.UTF_8), s -> {
            asReturned = s;
            return body;
        });
    }

    public String asReturned() {
        return asReturned;
    }
}
