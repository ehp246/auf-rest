package me.ehp246.test.local.mime.multipart;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = AppConfig.class, webEnvironment = WebEnvironment.RANDOM_PORT)
class MultipartTest {
    private static final String SAMPLE_FILE = "sample.txt";

    @Value("${local.server.port}")
    private String port;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MultipartCase multipartCase;

    @Test
    void upload_01() throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(new ClassPathResource(SAMPLE_FILE).getFile()));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        String serverUrl = "http://localhost:" + port + "/multipart/file";

        ResponseEntity<String> response = restTemplate.postForEntity(serverUrl, requestEntity, String.class);

        Assertions.assertEquals("a6982add-2433-431b-ac5c-54a31d4288cc", response.getBody());
    }

    @Test
    void upload_02() throws IOException {
        Assertions.assertEquals("a6982add-2433-431b-ac5c-54a31d4288cc",
                multipartCase.post(new ClassPathResource(SAMPLE_FILE).getFile().toPath()));
    }
}
