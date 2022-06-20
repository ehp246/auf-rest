package me.ehp246.aufrest.integration.local.auth;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import me.ehp246.aufrest.api.exception.UnhandledResponseException;
import me.ehp246.aufrest.api.rest.HeaderContext;

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
        Assertions.assertThrows(UnhandledResponseException.class,
                () -> factory.getBean(TestCases.DefaultCase001.class).get());
    }

    @Test
    void basic_auth_002() {
        final var newInstance = factory.getBean(TestCases.DefaultCase001.class);
        /*
         * If the return type is HttpResponse, the invocation should not throw as long
         * as a response is received and can be returned.
         */
        final var response = Assertions.assertDoesNotThrow(newInstance::getAsResponse,
                "Should return a valid response instead of throwing");

        Assertions.assertEquals(401, response.statusCode(), "Should have correct status code");
    }

    @Test
    void basic_auth_003() {
        final var newInstance = factory.getBean(TestCases.BearerCase003.class);

        Assertions.assertThrows(UnhandledResponseException.class, newInstance::get,
                "Should not work because of the wrong authentication type");

        /**
         * Should work now.
         */
        newInstance.get("Basic YmFzaWN1c2VyOnBhc3N3b3Jk");
    }

    @Test
    void basic_auth_004() {
        final var bean = factory.getBean(TestCases.SimpleCase004.class);

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
        Assertions.assertThrows(UnhandledResponseException.class,
                () -> factory.getBean(TestCases.DefaultCase001.class).get(""));
    }

    @Test
    void auth_header_002() {
        factory.getBean(TestCases.DefaultCase001.class).get("Basic YmFzaWN1c2VyOnBhc3N3b3Jk");
    }

    @Test
    void method_auth_001() {
        // Should follow the interface
        factory.getBean(TestCases.MethodAuthCase001.class).get();
    }
}
