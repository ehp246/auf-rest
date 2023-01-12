package me.ehp246.test.embedded.mime.multipart;

import java.nio.file.Path;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfRequest;

/**
 * @author Lei Yang
 *
 */
@ByRest(value = "http://localhost:${local.server.port}/multipart")
interface MultipartCase {
    @OfRequest(value = "/file", accept = "text/plain")
    String post(Path file);
}
