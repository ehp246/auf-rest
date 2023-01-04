package me.ehp246.test.embedded.method;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfMapping;

/**
 * @author Lei Yang
 *
 */
@ByRest("http://localhost:${local.server.port}/method")
interface TestCase001 {
    @OfMapping(accept = "text/plain")
    String get();

    @OfMapping(accept = "text/plain")
    String put();

    @OfMapping(accept = "text/plain")
    String post();

    @OfMapping(accept = "text/plain")
    String patch();

    @OfMapping(accept = "text/plain")
    String delete();

    String m001();

    @OfMapping(method = "get", accept = "text/plain")
    String m002();

    // Request should go out with the wrong method name.
    @OfMapping(method = "got", accept = "text/plain")
    String m003();

    // Should ignore the blank string and use prefix
    @OfMapping(accept = "text/plain", method = " ")
    String put001();
}
