package me.ehp246.test.embedded.exception.request;

import java.net.ConnectException;

import me.ehp246.aufrest.api.annotation.ByRest;

/**
 * @author Lei Yang
 *
 */
@ByRest(value = "http://localhost:0")
interface ErrorRequestTestCase {
    void get();

    void getThrowing() throws ConnectException;
}
