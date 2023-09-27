package me.ehp246.aufrest.core.rest;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfQuery;
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

        void getInBody(Name name);
        void getInBody(Name name, @OfQuery String zipCode);
        void getInBody(DupName name);

        void getInBody(Name name, Name old);

        void getOnBody(@OfThreadContext Name name);

    }

    record Name(@OfThreadContext String firstName, @OfThreadContext String lastName) {
        @OfThreadContext
        String fullName() {
            return firstName + lastName;
        }
    }

    record DupName(@OfThreadContext("name") String firstName, @OfThreadContext("name") String lastName) {
        @OfThreadContext("name")
        String fullName() {
            return firstName + lastName;
        }
    }
}
