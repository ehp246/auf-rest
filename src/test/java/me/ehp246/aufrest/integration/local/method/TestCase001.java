package me.ehp246.aufrest.integration.local.method;

import me.ehp246.aufrest.api.annotation.AsIs;
import me.ehp246.aufrest.api.annotation.ByRest;

/**
 * @author Lei Yang
 *
 */
@ByRest("http://localhost:${local.server.port}/method")
interface TestCase001 {
	@AsIs
	String get();

	@AsIs
	String put();

	@AsIs
	String post();

	@AsIs
	String patch();

	@AsIs
	String delete();
}
