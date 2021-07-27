package me.ehp246.aufrest.api.rest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Lei Yang
 *
 */
class BodyReceiverTest {

    @Test
    void test_001() {
        Assertions.assertEquals(Object.class, new BodyReceiver() {

            @Override
            public Class<?> type() {
                return null;
            }
        }.errorType());
    }

}
