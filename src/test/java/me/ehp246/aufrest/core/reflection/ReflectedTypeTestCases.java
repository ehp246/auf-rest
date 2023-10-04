package me.ehp246.aufrest.core.reflection;

import me.ehp246.aufrest.api.annotation.OfLog4jContext;

/**
 * @author Lei Yang
 *
 */
class ReflectedTypeTestCases {
    static class Case01 {
    }

    static record Case02(@OfLog4jContext String firstName, String middleName, @OfLog4jContext String lastName) {
        @OfLog4jContext("fullName")
        String toFullName() {
            return this.firstName + this.middleName + this.lastName;
        }
    }

    static record Case03() {
    }

    static class Case04 {
        @OfLog4jContext
        void m1() {
        }

        @OfLog4jContext
        Void m2() {
            return null;
        }

        @OfLog4jContext
        String m3(final String name) {
            return name;
        }

        @OfLog4jContext
        String m4() {
            return null;
        }
    }
}
