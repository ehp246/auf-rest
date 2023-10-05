package me.ehp246.aufrest.core.rest;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.ByRest.Executor;
import me.ehp246.aufrest.api.annotation.OfLog4jContext;
import me.ehp246.aufrest.api.annotation.OfQuery;

/**
 * @author Lei Yang
 *
 */
interface ThreadContextTestCases {
    @ByRest("")
    interface Case01 {
        void get();

        void get(@OfLog4jContext("name") String firstName, @OfLog4jContext("name") String lastName);

        void get(@OfLog4jContext String name, @OfLog4jContext("SSN") int id);

        void getInBody(Name name);

        void getInBody(Name name, @OfQuery String zipCode);

        void getInBody(DupName name);

        void getInBody(Name name, Name old);

        void getOnBody(@OfLog4jContext Name name);
    }

    @ByRest(value = "", executor = @Executor(log4jContext = { "context_1", "context_2" }))
    interface Case02 {
        void get();
    }

    record Name(@OfLog4jContext String firstName, @OfLog4jContext String lastName) {
        @OfLog4jContext
        String fullName() {
            return firstName + lastName;
        }
    }

    record DupName(@OfLog4jContext("name") String firstName, @OfLog4jContext("name") String lastName) {
        @OfLog4jContext("name")
        String fullName() {
            return firstName + lastName;
        }
    }
}
