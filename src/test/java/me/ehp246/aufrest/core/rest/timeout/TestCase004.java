package me.ehp246.aufrest.core.rest.timeout;

import me.ehp246.aufrest.api.annotation.ByRest;

/**
 * @author Lei Yang
 *
 */
@ByRest(value = "", timeout = "${api.timeout.5s}")
interface TestCase004 {
    void get();
}
