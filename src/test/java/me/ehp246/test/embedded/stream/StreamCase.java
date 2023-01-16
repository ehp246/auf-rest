package me.ehp246.test.embedded.stream;

import java.io.InputStream;
import java.net.http.HttpResponse;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfQuery;
import me.ehp246.aufrest.api.annotation.OfRequest;
import me.ehp246.aufrest.api.annotation.OfResponse;
import me.ehp246.aufrest.api.annotation.OfResponse.BodyOf;

/**
 * @author Lei Yang
 *
 */
interface StreamCase {
    @ByRest(value = "http://localhost:${local.server.port}")
    interface Case001 {
        @OfRequest("/person")
        InputStream get(@OfQuery("name") String name);

        @OfRequest("/person")
        @OfResponse(body = @BodyOf(InputStream.class))
        HttpResponse<InputStream> get002(@OfQuery("name") String name);

        @OfRequest(value = "/inputstream")
        Integer post(InputStream in);
    }

    @ByRest(value = "http://localhost:${local.server.port}", acceptGZip = false)
    interface Case002 {
        @OfRequest("/person")
        InputStream get(@OfQuery("name") String name);
    }
}
