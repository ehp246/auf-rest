package me.ehp246.aufrest.core.reflection;

import java.lang.invoke.MethodHandles;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import me.ehp246.test.InvocationUtil;

/**
 * @author Lei Yang
 *
 */
class ReflectedTypeTest {
    private static final MethodHandles.Lookup lookup = MethodHandles.lookup();

    @Test
    void test_01() throws IllegalAccessException {
        final var captor = InvocationUtil.newCaptor(ReflectedObjectTestCases.Case01.class);
        captor.proxy().get();

        final var invocation = captor.invocation();

        Assertions.assertEquals(lookup.unreflect(invocation.method()).type(),
                new ReflectedType(ReflectedObjectTestCases.Case01.class)
                        .findPublicMethod("get", void.class).get().type());
    }

    @Test
    void test_02() throws IllegalAccessException {
        final var captor = InvocationUtil.newCaptor(ReflectedObjectTestCases.Case01.class);
        captor.proxy().get(UUID.randomUUID().toString());

        final var invocation = captor.invocation();

        Assertions.assertEquals(lookup.unreflect(invocation.method()).type(),
                new ReflectedType(ReflectedObjectTestCases.Case01.class)
                        .findPublicMethod("get", String.class, new Class<?>[] { String.class }).get()
                        .type());
    }

    @Test
    void test_03() throws IllegalAccessException {
        final var captor = InvocationUtil.newCaptor(ReflectedObjectTestCases.Case01.class);
        captor.proxy().getFirstName(new ReflectedObjectTestCases.Person(null, null, null));

        final var invocation = captor.invocation();

        Assertions.assertEquals(lookup.unreflect(invocation.method()).type(),
                new ReflectedType(ReflectedObjectTestCases.Case01.class)
                        .findPublicMethod("getFirstName", String.class,
                                new Class<?>[] { ReflectedObjectTestCases.PersonName.class })
                        .get().type());
    }
}
