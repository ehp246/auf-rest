package me.ehp246.aufrest.core.byrest;

import me.ehp246.aufrest.api.annotation.ByRest;

/**
 * @author Lei Yang
 *
 */
@ByRest("")
interface InvokedOnTestCase001 {
	void get();

	void get(int i);
}
