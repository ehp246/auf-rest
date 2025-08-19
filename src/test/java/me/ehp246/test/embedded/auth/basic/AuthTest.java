package me.ehp246.test.embedded.auth.basic;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import me.ehp246.aufrest.api.exception.ClientErrorException;
import me.ehp246.aufrest.api.exception.ProxyInvocationBinderException;
import me.ehp246.aufrest.api.exception.UnhandledResponseException;
import me.ehp246.aufrest.api.rest.HeaderContext;
import me.ehp246.test.embedded.auth.basic.TestCases.BeanAuth02;
import me.ehp246.test.embedded.auth.basic.TestCases.BeanAuth03;
import me.ehp246.test.embedded.auth.basic.TestCases.BeanAuthThrowing01;
import me.ehp246.test.embedded.auth.basic.TestCases.BeanAuthThrowing02;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
class AuthTest {
    @Autowired
    private AutowireCapableBeanFactory factory;

    @BeforeAll
    static void clear() {
        HeaderContext.clear();
    }

    @Test
    void basic_auth_001() {
        final var bean = factory.getBean(TestCases.Default01.class);
        Assertions.assertThrows(UnhandledResponseException.class, () -> bean.get());
    }

    @Test
    void basic_auth_002() {
        final var newInstance = factory.getBean(TestCases.Default01.class);
        final var response = Assertions.assertThrows(UnhandledResponseException.class, newInstance::getAsResponse,
                "Should return a valid response instead of throwing");

        Assertions.assertEquals(401, response.statusCode(), "Should have correct status code");
    }

    @Test
    void basic_auth_003() {
        final var newInstance = factory.getBean(TestCases.Bearer03.class);

        Assertions.assertThrows(UnhandledResponseException.class, newInstance::get,
                "Should not work because of the wrong authentication type");

        /**
         * Should work now.
         */
        newInstance.get("Basic YmFzaWN1c2VyOnBhc3N3b3Jk");
    }

    @Test
    void basic_auth_004() {
        final var bean = factory.getBean(TestCases.Simple04.class);

        bean.get();

        Assertions.assertThrows(UnhandledResponseException.class, () -> bean.get("123"),
                "Should not work because of the wrong header");

        /**
         * Should work now.
         */
        bean.get("Basic YmFzaWN1c2VyOnBhc3N3b3Jk");
    }

    @Test
    void auth_header_001() {
        final var bean = factory.getBean(TestCases.Default01.class);
        Assertions.assertThrows(UnhandledResponseException.class, () -> bean.get(""));
    }

    @Test
    void auth_header_002() {
        final var bean = factory.getBean(TestCases.Default01.class);
        Assertions.assertDoesNotThrow(() -> bean.get("Basic YmFzaWN1c2VyOnBhc3N3b3Jk"));
    }

    @Test
    void method_auth_001() {
        // Should follow the interface
        final var bean = factory.getBean(TestCases.MethodAuth01.class);
        Assertions.assertDoesNotThrow(() -> bean.get());
    }

    @Test
    void authBean_02() {
        final var bean = factory.getBean(BeanAuth02.class);
        Assertions.assertDoesNotThrow(() -> bean.get("basicuser", "password"));
    }

    @Test
    void authBean_03() {
        final var actual = Assertions.assertThrows(ClientErrorException.class,
                () -> factory.getBean(BeanAuth03.class).get("basicuser", "password"));

        Assertions.assertEquals(401, actual.statusCode());
    }

    @Test
    void authBean_throwing_01() {
        final var expected = new RuntimeException();

        final var bean = factory.getBean(BeanAuthThrowing01.class);
        Assertions.assertEquals(expected, Assertions.assertThrows(RuntimeException.class, () -> bean.get(expected)));
    }

    @Test
    void authBean_throwing_02() {
        final var expected = new IOException();

        final var bean = factory.getBean(BeanAuthThrowing02.class);
        Assertions.assertEquals(expected,
                Assertions.assertThrows(IOException.class, () -> bean.getWithThrows(expected)));
    }

    @Test
    void authBean_throwing_03() {
        final var expected = new IllegalAccessException();

        final var bean = factory.getBean(BeanAuthThrowing02.class);
        Assertions.assertEquals(expected,
                Assertions.assertThrows(ProxyInvocationBinderException.class, () -> bean.get(expected)).getCause());
    }

    @Test
    void authBean_throwing_04() {
        final var expected = new IllegalAccessException();

        Assertions
                .assertEquals(expected,
                        Assertions
                                .assertThrows(ProxyInvocationBinderException.class,
                                        () -> factory.getBean(BeanAuthThrowing02.class).getWithThrows(expected))
                                .getCause());
    }
}
