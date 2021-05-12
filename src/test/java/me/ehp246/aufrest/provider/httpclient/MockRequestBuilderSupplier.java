package me.ehp246.aufrest.provider.httpclient;

import java.net.http.HttpRequest;
import java.net.http.HttpRequest.Builder;
import java.util.function.Supplier;

/**
 * @author Lei Yang
 *
 */
public class MockRequestBuilderSupplier implements Supplier<HttpRequest.Builder> {

    @Override
    public Builder get() {
        return HttpRequest.newBuilder();
    }

}
