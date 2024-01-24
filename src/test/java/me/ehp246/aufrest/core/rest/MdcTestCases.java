package me.ehp246.aufrest.core.rest;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.ByRest.Executor;

/**
 * @author Lei Yang
 *
 */
interface MdcTestCases {
    @ByRest(value = "", executor = @Executor(mdc = { "context_1", "context_2" }))
    interface Case02 {
        void get();
    }
}