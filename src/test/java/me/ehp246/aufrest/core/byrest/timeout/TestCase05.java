package me.ehp246.aufrest.core.byrest.timeout;

import me.ehp246.aufrest.api.annotation.ByRest;

/**
 * @author Lei Yang
 *
 */
@ByRest(value = "", timeout = "${api.timeout.illegal}")
interface TestCase05 {
    void get();
}
