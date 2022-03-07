package me.ehp246.aufrest.core.byrest.requestbody;

import java.io.InputStream;
import java.net.http.HttpRequest.BodyPublisher;

import me.ehp246.aufrest.api.annotation.ByRest;

/**
 * @author Lei Yang
 *
 */
@ByRest("")
interface RequestBodyTestCase01 {
    void get(BodyPublisher body);

    void get(InputStream body);
}
