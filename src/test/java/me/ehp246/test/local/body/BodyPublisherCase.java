package me.ehp246.test.local.body;

import java.net.http.HttpRequest.BodyPublisher;
import java.util.List;

import org.springframework.http.MediaType;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfMapping;

/**
 * @author Lei Yang
 *
 */
@ByRest(value = "http://localhost:${local.server.port}/body")
interface BodyPublisherCase {
    @OfMapping("/publisher")
    List<String> post(BodyPublisher publisher);

    @OfMapping(value = "/publisher/query", contentType = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    List<String> postQueryParams(BodyPublisher publisher);
}
