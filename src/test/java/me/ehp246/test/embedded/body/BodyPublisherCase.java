package me.ehp246.test.embedded.body;

import java.net.http.HttpRequest.BodyPublisher;
import java.util.List;

import org.springframework.http.MediaType;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfRequest;
import me.ehp246.aufrest.api.rest.HttpUtils;

/**
 * @author Lei Yang
 *
 */
@ByRest(value = "http://localhost:${local.server.port}/body")
interface BodyPublisherCase {
    @OfRequest("/publisher")
    List<String> post(BodyPublisher publisher);

    @OfRequest(value = "/publisher", contentType = HttpUtils.APPLICATION_JSON)
    List<String> postAsJson(BodyPublisher publisher);

    @OfRequest(value = "/publisher/query", contentType = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    List<String> postQueryParams(BodyPublisher publisher);
}
