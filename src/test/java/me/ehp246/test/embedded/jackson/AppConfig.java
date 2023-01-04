package me.ehp246.test.embedded.jackson;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.mrbean.MrBeanModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import me.ehp246.aufrest.api.annotation.EnableByRest;

/**
 * @author Lei Yang
 *
 */
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EnableByRest
class AppConfig {
    @Bean
    ObjectMapper objectMapper() {
        return JsonMapper.builder().configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).build()
                .setSerializationInclusion(Include.NON_NULL).registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).registerModule(new MrBeanModule())
                .registerModule(new ParameterNamesModule());
    }
}
