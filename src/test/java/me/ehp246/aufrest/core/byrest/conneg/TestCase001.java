package me.ehp246.aufrest.core.byrest.conneg;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfMapping;
import me.ehp246.aufrest.api.rest.HttpUtils;

/**
 * @author Lei Yang
 *
 */
@ByRest("")
interface TestCase001 {
	void get();

	@OfMapping(produces = HttpUtils.TEXT_PLAIN, consumes = HttpUtils.TEXT_PLAIN)
	void put();

	@OfMapping(produces = "i produce", consumes = "i accept")
	void post();
}
