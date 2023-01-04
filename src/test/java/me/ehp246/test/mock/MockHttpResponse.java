package me.ehp246.test.mock;

import java.net.URI;
import java.net.http.HttpClient.Version;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Optional;

import javax.net.ssl.SSLSession;

/**
 * @author Lei Yang
 *
 */
public class MockHttpResponse<T> implements HttpResponse<T> {
    private final int statusCode;
    private final T body;

    public MockHttpResponse() {
        super();
        this.statusCode = 200;
        this.body = null;
    }

    public MockHttpResponse(final int statusCode) {
        super();
        this.statusCode = statusCode;
        this.body = null;
    }

    public MockHttpResponse(final int statusCode, final T body) {
        super();
        this.statusCode = statusCode;
        this.body = body;
    }

    public MockHttpResponse(final T body) {
        super();
        this.statusCode = 200;
        this.body = body;
    }

    @Override
    public int statusCode() {
        return statusCode;
    }

    @Override
    public HttpRequest request() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<HttpResponse<T>> previousResponse() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HttpHeaders headers() {
        return HttpHeaders.of(Map.of(), (a, b) -> true);
    }

    @Override
    public T body() {
        return body;
    }

    @Override
    public Optional<SSLSession> sslSession() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public URI uri() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Version version() {
        // TODO Auto-generated method stub
        return null;
    }
}
