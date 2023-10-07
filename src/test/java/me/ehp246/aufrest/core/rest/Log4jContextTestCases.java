package me.ehp246.aufrest.core.rest;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.ByRest.Executor;

/**
 * @author Lei Yang
 *
 */
interface Log4jContextTestCases {
    @ByRest(value = "", executor = @Executor(log4jContext = { "context_1", "context_2" }))
    interface Case02 {
        void get();
    }
}