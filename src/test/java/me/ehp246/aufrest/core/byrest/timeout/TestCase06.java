package me.ehp246.aufrest.core.byrest.timeout;

import me.ehp246.aufrest.api.annotation.ByRest;

/**
 * @author Lei Yang
 *
 */
@ByRest(value = "", timeout = "${api.timeout.missing}")
interface TestCase06 {
    void get();
}
