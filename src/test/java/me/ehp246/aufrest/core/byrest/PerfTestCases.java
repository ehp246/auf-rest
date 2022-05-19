package me.ehp246.aufrest.core.byrest;

import me.ehp246.aufrest.api.annotation.AuthHeader;
import me.ehp246.aufrest.api.annotation.ByRest;

/**
 * @author Lei Yang
 *
 */
class PerfTestCases {
    @ByRest("")
    interface Case001 {
        void get();

        void get(@AuthHeader String auth, String body);
    }
}
