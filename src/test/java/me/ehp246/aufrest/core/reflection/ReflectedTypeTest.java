package me.ehp246.aufrest.core.reflection;

import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import me.ehp246.aufrest.api.annotation.OfThreadContext;
import me.ehp246.aufrest.core.util.OneUtil;

/**
 * @author Lei Yang
 *
 */
class ReflectedTypeTest {
    @Test
    void annotated_supplier_method_01() {
        Assertions.assertEquals(0, new ReflectedType<>(ReflectedTypeTestCases.Case01.class)
                .streamSuppliersWith(OfThreadContext.class).collect(Collectors.toSet()).size());

        Assertions.assertEquals(3, new ReflectedType<>(ReflectedTypeTestCases.Case02.class)
                .streamSuppliersWith(OfThreadContext.class).collect(Collectors.toSet()).size());

        Assertions.assertEquals(0, new ReflectedType<>(ReflectedTypeTestCases.Case03.class)
                .streamSuppliersWith(OfThreadContext.class).collect(Collectors.toSet()).size());

        Assertions.assertEquals(1, new ReflectedType<>(ReflectedTypeTestCases.Case04.class)
                .streamSuppliersWith(OfThreadContext.class).collect(Collectors.toSet()).size());
    }

    @Test
    void annotated_supplier_method_02() {
        final var suppliers = new ReflectedType<>(ReflectedTypeTestCases.Case02.class)
                .supplierBindersWith(OfThreadContext.class, m -> {
                    final var name = m.getAnnotation(OfThreadContext.class).value();
                    return OneUtil.hasValue(name) ? name : m.getName();
                });
        final var expected = new ReflectedTypeTestCases.Case02(UUID.randomUUID().toString(), null,
                UUID.randomUUID().toString());

        Assertions.assertEquals(3, suppliers.size());
        Assertions.assertEquals(expected.firstName(), suppliers.get("firstName").apply(expected));
        Assertions.assertEquals(expected.lastName(), suppliers.get("lastName").apply(expected));
        Assertions.assertEquals(expected.toFullName(), suppliers.get("fullName").apply(expected));
    }
}
