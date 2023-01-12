package me.ehp246.test.storage;

import java.io.InputStream;
import java.nio.file.Path;

import org.springframework.web.bind.annotation.PathVariable;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfRequest;
import me.ehp246.aufrest.api.rest.HttpUtils;

/**
 * @author Lei Yang
 *
 */
@ByRest(value = "${local.blob.url}/{file-name}?${local.blob.sas}", headers = { "x-ms-blob-type", "BlockBlob", })
interface BlobContainer {
    void put(@PathVariable("file-name") String name, Path file);

    @OfRequest(value = "", contentType = HttpUtils.TEXT_PLAIN)
    void put(@PathVariable("file-name") String name, InputStream file);

    String get(@PathVariable("file-name") String name);
}
