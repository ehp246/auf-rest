package me.ehp246.aufrest.core.byrest.timeout;

import me.ehp246.aufrest.api.annotation.ByRest;

/**
 * @author Lei Yang
 *
 */
@ByRest(value = "", timeout = "pt0.01s")
interface TestCase007 {
    void get();
}
