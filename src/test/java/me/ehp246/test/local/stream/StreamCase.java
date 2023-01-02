package me.ehp246.test.local.stream;

import java.io.InputStream;
import java.net.http.HttpResponse;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfBody;
import me.ehp246.aufrest.api.annotation.OfMapping;
import me.ehp246.aufrest.api.annotation.OfQuery;

/**
 * @author Lei Yang
 *
 */
interface StreamCase {
    @ByRest(value = "http://localhost:${local.server.port}")
    interface Case001 {
        @OfMapping("/person")
        InputStream get(@OfQuery("name") String name);

        @OfMapping("/person")
        @OfBody(InputStream.class)
        HttpResponse<InputStream> get002(@OfQuery("name") String name);

        @OfMapping(value = "/inputstream")
        Integer post(InputStream in);
    }

    @ByRest(value = "http://localhost:${local.server.port}", acceptGZip = false)
    interface Case002 {
        @OfMapping("/person")
        InputStream get(@OfQuery("name") String name);
    }
}
