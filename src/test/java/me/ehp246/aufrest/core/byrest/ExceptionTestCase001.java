package me.ehp246.aufrest.core.byrest;

import java.io.IOException;
import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpTimeoutException;

import me.ehp246.aufrest.api.annotation.ByRest;

/**
 * @author Lei Yang
 *
 */
@ByRest("")
interface ExceptionTestCase001 {
    void delete();

    void get() throws IOException;

    void put() throws HttpTimeoutException;

    void post() throws HttpConnectTimeoutException;
}
