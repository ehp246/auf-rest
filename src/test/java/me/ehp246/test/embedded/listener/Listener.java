/**
 *
 */
package me.ehp246.test.embedded.listener;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import me.ehp246.aufrest.api.rest.RestListener;
import me.ehp246.aufrest.api.rest.RestRequest;

/**
 * @author Lei Yang
 *
 */
class Listener implements RestListener {
    private final int id;

    private HttpRequest httpReq;
    private HttpResponse<?> httpResponse;
    private RestRequest reqReq;

    public Listener(final int id) {
        super();
        this.id = id;
    }

    void clear() {
        this.httpReq = null;
        this.httpResponse = null;
        this.reqReq = null;
    }

    RestRequest reqByReq() {
        return this.reqReq;
    }

    HttpRequest httpReq() {
        return this.httpReq;
    }

    int id() {
        return this.id;
    }

    @Override
    public void onRequest(final HttpRequest httpRequest, final RestRequest req) {
        this.httpReq = httpRequest;
        this.reqReq = req;
    }

    @Override
    public void onResponse(final HttpResponse<?> httpResponse, final RestRequest req) {
        this.httpResponse = httpResponse;
        this.reqReq = req;
    }

    @Override
    public void onException(final Exception exception, final HttpRequest httpRequest, final RestRequest req) {
    }

    /**
     * @return the httpResponse
     */
    public HttpResponse<?> getHttpResponse() {
        return httpResponse;
    }
}
