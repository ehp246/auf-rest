package me.ehp246.aufrest.core.reflection;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Lei Yang
 *
 */
class ReflectedObjectTest {
    private static final ReflectedObjectTestCases.Case01 case01 = new ReflectedObjectTestCases.Case01() {
    };

    @Test
    void test_01() throws IllegalAccessException {
        Assertions.assertEquals(true,
                new ReflectedObject(case01).findPublicMethod("get", void.class, null).isPresent());
    }

    @Test
    void test_02() throws IllegalAccessException {
        Assertions.assertEquals(true,
                new ReflectedObject(case01).findPublicMethod("get", String.class, List.of(String.class)).isPresent());
    }

    @Test
    void test_03() throws IllegalAccessException {
        Assertions.assertEquals(true, new ReflectedObject(case01)
                .findPublicMethod("getFirstName", String.class, List.of(ReflectedObjectTestCases.PersonName.class))
                .isPresent());
    }
}
