package me.ehp246.test.app.bean;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

import me.ehp246.aufrest.api.annotation.EnableByRest;
import me.ehp246.test.mock.Jackson;

@EnableByRest
@Import(Jackson.class)
@EnableConfigurationProperties({ AppConfig.RestServerConfig.class })
class AppConfig {
    @ConfigurationProperties(prefix = "rest.server")
    static record RestServerConfig(String api) {
    }
}
