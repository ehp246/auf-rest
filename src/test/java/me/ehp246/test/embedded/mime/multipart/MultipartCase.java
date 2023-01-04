package me.ehp246.test.embedded.mime.multipart;

import java.nio.file.Path;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfMapping;

/**
 * @author Lei Yang
 *
 */
@ByRest(value = "http://localhost:${local.server.port}/multipart")
interface MultipartCase {
    @OfMapping(value = "/file", accept = "text/plain")
    String post(Path file);
}
