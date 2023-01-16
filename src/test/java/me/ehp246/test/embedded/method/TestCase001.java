package me.ehp246.test.embedded.method;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfRequest;

/**
 * @author Lei Yang
 *
 */
@ByRest("http://localhost:${local.server.port}/method")
interface TestCase001 {
    @OfRequest(accept = "text/plain")
    String get();

    @OfRequest(accept = "text/plain")
    String put();

    @OfRequest(accept = "text/plain")
    String post();

    @OfRequest(accept = "text/plain")
    String patch();

    @OfRequest(accept = "text/plain")
    String delete();

    String m001();

    @OfRequest(method = "get", accept = "text/plain")
    String m002();

    // Request should go out with the wrong method name.
    @OfRequest(method = "got", accept = "text/plain")
    String m003();

    // Should ignore the blank string and use prefix
    @OfRequest(accept = "text/plain", method = " ")
    String put001();
}
