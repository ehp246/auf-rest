package me.ehp246.aufrest.integration.local.timeout;

import me.ehp246.aufrest.api.annotation.ByRest;

/**
 * @author Lei Yang
 *
 */
@ByRest("http://nowhere.com")
interface GlobalTestCase01 {
	void get();
}
