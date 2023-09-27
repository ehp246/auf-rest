package me.ehp246.aufrest.core.reflection;

import me.ehp246.aufrest.api.annotation.OfThreadContext;

/**
 * @author Lei Yang
 *
 */
class ReflectedTypeTestCases {
    static class Case01 {
    }

    static record Case02(@OfThreadContext String firstName, String middleName, @OfThreadContext String lastName) {
        @OfThreadContext("fullName")
        String toFullName() {
            return this.firstName + this.middleName + this.lastName;
        }
    }

    static record Case03() {
    }

    static class Case04 {
        @OfThreadContext
        void m1() {
        }

        @OfThreadContext
        Void m2() {
            return null;
        }

        @OfThreadContext
        String m3(final String name) {
            return name;
        }

        @OfThreadContext
        String m4() {
            return null;
        }
    }
}
