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
    private RestRequest reqResp;

    public Listener(int id) {
        super();
        this.id = id;
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
    public void onRequest(HttpRequest httpRequest, RestRequest req) {
        this.httpReq = httpRequest;
        this.reqReq = req;
    }

    @Override
    public void onResponse(HttpResponse<?> httpResponse, RestRequest req) {
        this.httpResponse = httpResponse;
        this.reqResp = req;
    }

    @Override
    public void onException(Exception exception, HttpRequest httpRequest, RestRequest req) {
    }

    /**
     * @return the httpResponse
     */
    public HttpResponse<?> getHttpResponse() {
        return httpResponse;
    }

    /**
     * @return the reqResp
     */
    public RestRequest getReqResp() {
        return reqResp;
    }
}
