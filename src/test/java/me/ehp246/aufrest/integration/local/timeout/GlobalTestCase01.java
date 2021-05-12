package me.ehp246.aufrest.integration.local.timeout;

import me.ehp246.aufrest.api.annotation.ByRest;

/**
 * @author Lei Yang
 *
 */
@ByRest("https://github.com")
interface GlobalTestCase01 {
    void get();
}
