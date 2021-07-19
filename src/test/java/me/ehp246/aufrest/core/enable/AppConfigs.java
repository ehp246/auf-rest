package me.ehp246.aufrest.core.enable;

import java.time.Instant;

import me.ehp246.aufrest.api.annotation.EnableByRest;

/**
 * @author Lei Yang
 *
 */
class AppConfigs {
    @EnableByRest
    static class Case01 {

    }

    @EnableByRest(errorType = Instant.class)
    static class Case02 {

    }
}
