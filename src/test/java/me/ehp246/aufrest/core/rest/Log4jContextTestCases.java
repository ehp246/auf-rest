package me.ehp246.aufrest.core.rest;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.ByRest.Executor;
import me.ehp246.aufrest.api.annotation.OfHeader;
import me.ehp246.aufrest.api.annotation.OfLog4jContext;
import me.ehp246.aufrest.api.annotation.OfLog4jContext.OP;

/**
 * @author Lei Yang
 *
 */
interface Log4jContextTestCases {
    @ByRest("")
    interface Case01 {
        void get();

        void get(@OfLog4jContext("name") @OfHeader String firstName, @OfLog4jContext("name") @OfHeader String lastName);

        void get(@OfLog4jContext @OfHeader String name, @OfLog4jContext("SSN") @OfHeader int id);

        void getBody(Name name);

        void getOnBodyToString(@OfLog4jContext Name name);

        void getOnBodyToStringWithName(@OfLog4jContext("withName") Name name);

        void getOnBodyIntrospect(@OfLog4jContext(op = OP.Introspect) Name name);

        void getOnBodyIntrospectWithName(@OfLog4jContext(value = "WithName", op = OP.Introspect) Name name);
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
