package me.ehp246.aufrest.core.reflection;

import java.time.Instant;

/**
 * @author Lei Yang
 *
 */
final class ReflectedObjectTestCases {
    interface Case01 {
        default void get() {
        }

        default String get(String id) {
            return id;
        }

        default String getFirstName(PersonName personName) {
            return personName.firstName();
        }

        default void get(String id, int i) {
        }
    }

    interface PersonName {
        String firstName();

        String lastName();
    }

    interface Dob {
        Instant dob();
    }

    record Person(String firstName, String lastName, Instant dob) implements PersonName, Dob {
    }
}
