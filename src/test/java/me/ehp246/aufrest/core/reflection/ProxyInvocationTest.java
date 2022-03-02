package me.ehp246.aufrest.core.reflection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Lei Yang
 *
 */
class ProxyInvocationTest {
    @Test
    void scanArgs_01() {
        final var annotatedArguments = new ProxyInvocation(TestCase.class, new Object(),
                ReflectionUtils.findMethod(TestCase.class, "scanArgs", String.class),
                new Object[] { "arg1" }).findArgumentOfAnnotation(RequestParam.class, RequestParam::value);

        Assertions.assertEquals(1, annotatedArguments.size());
        Assertions.assertEquals(true, annotatedArguments.get("query 1").equals("arg1"));
    }

    @Test
    void scanArgs_02() {
        final var annotatedArguments = new ProxyInvocation(TestCase.class, new Object(),
                ReflectionUtils.findMethod(TestCase.class, "scanArgs02", String.class,
                String.class),
                new Object[] { "arg1", null }).findArgumentOfAnnotation(RequestParam.class, RequestParam::value);

        Assertions.assertEquals(2, annotatedArguments.size());

        Assertions.assertEquals(true, annotatedArguments.get("query 1").equals("arg1"));
        Assertions.assertEquals(true, annotatedArguments.get("query 2") == null);
    }

    @Test
    void scanArgs_03() {
        final var annotatedArguments = new ProxyInvocation(TestCase.class, new Object(),
                ReflectionUtils.findMethod(TestCase.class, "scanArgs03", String.class, String.class),
                new Object[] { "arg1", null }).<String, String, RequestParam>mapArgumentsOfAnnotation(
                        RequestParam.class, RequestParam::value);

        Assertions.assertEquals(1, annotatedArguments.size(), "Should collect all arguments that have the same key");
        Assertions.assertEquals(2, annotatedArguments.get("query 1").size());

        Assertions.assertEquals("arg1", annotatedArguments.get("query 1").get(0));
        Assertions.assertEquals(null, annotatedArguments.get("query 1").get(1));

    }
}
