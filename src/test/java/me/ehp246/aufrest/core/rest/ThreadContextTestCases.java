package me.ehp246.aufrest.core.rest;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfThreadContext;

/**
 * @author Lei Yang
 *
 */
interface ThreadContextTestCases {
    @ByRest("")
    interface Case01 {
        void get();

        /**
         * Should throw?
         */
        void get(@OfThreadContext("name") String firstName, @OfThreadContext("name") String lastName);

        void get(@OfThreadContext String name, @OfThreadContext("SSN") int id);

        void getInBody(Person person);

        void getOnBody(@OfThreadContext Person person);
    }

    record Person(@OfThreadContext String firstName, @OfThreadContext String lastName) {
    }
}
