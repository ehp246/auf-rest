package me.ehp246.test.app.objectmapper;

import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.ehp246.aufrest.api.annotation.EnableByRest;
import me.ehp246.test.mock.Jackson;

/**
 * @author Lei Yang
 *
 */
class AppConfig {
    @EnableByRest
    static class Config01 {
        @Bean
        public ObjectMapper aufRestObjectMapper() {
            return Jackson.OBJECT_MAPPER;
        }
    }

    @EnableByRest
    static class Config02 {
        @Bean
        public ObjectMapper objectMapper() {
            return Jackson.OBJECT_MAPPER;
        }
    }

    @EnableByRest
    static class Config03 {
    }

    @EnableByRest
    static class Config04 {
        @Bean
        public ObjectMapper aufRestObjectMapper() {
            return Jackson.OBJECT_MAPPER;
        }

        @Bean
        public ObjectMapper objectMapper() {
            return Jackson.OBJECT_MAPPER;
        }
    }
}
