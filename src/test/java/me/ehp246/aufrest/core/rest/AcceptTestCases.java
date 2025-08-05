package me.ehp246.aufrest.core.rest;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfRequest;

/**
 * @author Lei Yang
 * 
 */
interface AcceptTestCases {

    @ByRest("")
    interface Case01 {
        @OfRequest(accept = "")
        void get1();

        void get2();
    }

    @ByRest(value = "", accept = "")
    interface Case02 {
        @OfRequest
        void get1();

        void get2();
    }
}
