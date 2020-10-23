package me.ehp246.aufrest.integration.local.method;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfMapping;

/**
 * @author Lei Yang
 *
 */
@ByRest("http://localhost:${local.server.port}/method")
interface TestCase001 {
	@OfMapping(consumes = "text/plain")
	String get();

	@OfMapping(consumes = "text/plain")
	String put();

	@OfMapping(consumes = "text/plain")
	String post();

	@OfMapping(consumes = "text/plain")
	String patch();

	@OfMapping(consumes = "text/plain")
	String delete();

	String m001();

	@OfMapping(method = "get", consumes = "text/plain")
	String m002();

	// Request should go out with the wrong method name.
	@OfMapping(method = "got", consumes = "text/plain")
	String m003();

	// Should ignore the blank string and use prefix
	@OfMapping(consumes = "text/plain", method = " ")
	String put001();
}
