package me.ehp246.test.embedded.exception.request;

import java.net.ConnectException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import me.ehp246.aufrest.api.exception.RestFnException;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class }, webEnvironment = WebEnvironment.NONE, properties = {
        "me.ehp246.aufrest.restlogger.enabled=false" })
class ErrorRequestTest {
    @Autowired
    private ErrorRequestTestCase case01;

    @Test
    void restFn_01() {
        Assertions.assertThrows(RestFnException.class, case01::get);
    }

    @Test
    void throwing_01() {
        Assertions.assertThrows(ConnectException.class, case01::getThrowing);
    }
}
