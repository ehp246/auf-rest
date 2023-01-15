package me.ehp246.test.embedded.auth.basic;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("authProviderEx")
class AuthProviderExTest {
    @Autowired
    private AppConfig appConfig;

    @Autowired
    private TestCases.Default01 case001;

    @Test
    void test_001() {
        final var ex = Assertions.assertThrows(NullPointerException.class, case001::get);

        Assertions.assertEquals(appConfig.ex, ex, "should propogate here");
    }
}
