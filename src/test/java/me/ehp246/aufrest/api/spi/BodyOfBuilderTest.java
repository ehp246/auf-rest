package me.ehp246.aufrest.api.spi;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Lei Yang
 *
 */
class BodyOfBuilderTest {

    @Test
    void test_01() {
        final var bodyOf = BodyOfBuilder.of(String.class);

        Assertions.assertEquals(1, bodyOf.reifying().size());
        Assertions.assertEquals(String.class, bodyOf.first());
        Assertions.assertEquals(null, bodyOf.view());
    }

    @Test
    void test_02() {
        final var bodyOf = BodyOfBuilder.of(Set.class, List.class, String.class);

        Assertions.assertEquals(3, bodyOf.reifying().size());
        Assertions.assertEquals(Set.class, bodyOf.reifying().get(0));
        Assertions.assertEquals(List.class, bodyOf.reifying().get(1));
        Assertions.assertEquals(String.class, bodyOf.reifying().get(2));
        Assertions.assertEquals(null, bodyOf.view());
    }

    @Test
    void test_03() {
        final var bodyOf = BodyOfBuilder.ofView(String.class, String.class);

        Assertions.assertEquals(1, bodyOf.reifying().size());
        Assertions.assertEquals(String.class, bodyOf.first());
        Assertions.assertEquals(String.class, bodyOf.view());
    }

    @Test
    void test_04() {
        final var bodyOf = BodyOfBuilder.ofView(Integer.class, String.class, String.class);

        Assertions.assertEquals(Integer.class, bodyOf.view());
        Assertions.assertEquals(2, bodyOf.reifying().size());
        Assertions.assertEquals(String.class, bodyOf.first());
    }

    @Test
    void test_05() {
        final var bodyOf = BodyOfBuilder.of(Integer.class, String.class, String.class);

        Assertions.assertThrows(UnsupportedOperationException.class, bodyOf.reifying()::clear);
    }

    @Test
    void test_06() {
        Assertions.assertThrows(NullPointerException.class, () -> BodyOfBuilder.of(null));
        Assertions.assertThrows(NullPointerException.class, () -> BodyOfBuilder.of(null, (Class<?>) null));

        Assertions.assertThrows(NullPointerException.class, () -> BodyOfBuilder.ofView(String.class, null));
        Assertions.assertThrows(NullPointerException.class,
                () -> BodyOfBuilder.ofView(String.class, null, (Class<?>) null));
    }
}
