package me.ehp246.test.mock;

import java.net.URI;
import java.net.http.HttpClient.Version;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.net.ssl.SSLSession;

import org.mockito.Mockito;

/**
 * @author Lei Yang
 *
 */
public class MockHttpResponse<T> implements HttpResponse<T> {
    private final int statusCode;
    private final T body;
    private final Map<String, List<String>> headers;
    private final HttpRequest httpRequest = Mockito.mock(HttpRequest.class);

    public MockHttpResponse() {
        super();
        this.statusCode = 200;
        this.body = null;
        this.headers = Map.of();
    }

    public MockHttpResponse(final int statusCode) {
        super();
        this.statusCode = statusCode;
        this.body = null;
        this.headers = Map.of();
    }

    public MockHttpResponse(final int statusCode, final T body) {
        super();
        this.statusCode = statusCode;
        this.body = body;
        this.headers = Map.of();
    }

    public MockHttpResponse(final int statusCode, final T body, final Map<String, List<String>> headers) {
        super();
        this.statusCode = statusCode;
        this.body = body;
        this.headers = headers;
    }

    public MockHttpResponse(final T body) {
        super();
        this.statusCode = 200;
        this.body = body;
        this.headers = Map.of();
    }

    @Override
    public int statusCode() {
        return statusCode;
    }

    @Override
    public HttpRequest request() {
        return this.httpRequest;
    }

    @Override
    public Optional<HttpResponse<T>> previousResponse() {
        return null;
    }

    @Override
    public HttpHeaders headers() {
        return HttpHeaders.of(this.headers, (a, b) -> true);
    }

    @Override
    public T body() {
        return body;
    }

    @Override
    public Optional<SSLSession> sslSession() {
        return null;
    }

    @Override
    public URI uri() {
        return null;
    }

    @Override
    public Version version() {
        return null;
    }
}
