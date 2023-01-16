package me.ehp246.aufrest.core.rest.returntype;

import java.time.Instant;

import me.ehp246.aufrest.api.annotation.ByRest;

/**
 * @author Lei Yang
 *
 */
interface ErrorTypeCases {
    @ByRest("")
    interface Case001 {
        void get();
    }
    
    @ByRest(value = "", errorType = Instant.class)
    interface Case002 {
        void get();
    }
}
