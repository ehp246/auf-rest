package me.ehp246.test.embedded.mime.multipart;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Lei Yang
 *
 */
@RestController
@RequestMapping(value = "multipart", produces = MediaType.APPLICATION_JSON_VALUE)
class MultipoartController {
    @PostMapping(value = "file", produces = MediaType.TEXT_PLAIN_VALUE)
    String postString(@RequestParam(value = "file") MultipartFile file) throws IOException {
        return new String(file.getBytes());
    }
}
