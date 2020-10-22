package me.ehp246.aufrest.integration.local.returntype;

import java.time.Instant;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfMapping;
import me.ehp246.aufrest.api.rest.HttpUtils;

/**
 * @author Lei Yang
 *
 */
@ByRest("http://localhost:${local.server.port}/returntype")
interface TextTestCase001 {
	@OfMapping(value = "/instant", consumes = HttpUtils.TEXT_PLAIN)
	String get();

	@OfMapping(value = "/instant", produces = "text/plain", consumes = HttpUtils.TEXT_PLAIN)
	String post(String text);

	@OfMapping(value = "/instant", method = "POST", produces = "application/json", consumes = HttpUtils.TEXT_PLAIN)
	String post(Instant instant);
}
