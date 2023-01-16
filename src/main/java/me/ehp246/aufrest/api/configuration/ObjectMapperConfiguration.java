package me.ehp246.aufrest.api.configuration;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.ClassUtils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import me.ehp246.aufrest.core.rest.AufRestConfiguration;

/**
 * Factory used to optionally register an
 * {@linkplain AufRestConstants#BEAN_OBJECT_MAPPER} bean.
 *
 * @author Lei Yang
 * @see AufRestConfiguration#postProcessBeanDefinitionRegistry(org.springframework.beans.factory.support.BeanDefinitionRegistry)
 */
public final class ObjectMapperConfiguration {
    private final static Logger LOGGER = LogManager.getLogger();

    private final static List<String> MODULES = List.of(
            "com.fasterxml.jackson.datatype.jsr310.JavaTimeModule",
            "com.fasterxml.jackson.module.mrbean.MrBeanModule",
            "com.fasterxml.jackson.module.paramnames.ParameterNamesModule");

    public ObjectMapper aufRestObjectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper().setSerializationInclusion(Include.NON_NULL)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        for (final var name : MODULES) {
            if (ClassUtils.isPresent(name, this.getClass().getClassLoader())) {
                try {
                    objectMapper.registerModule((Module) Class.forName(name).getDeclaredConstructor((Class[]) null)
                            .newInstance((Object[]) null));
                } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                        | InvocationTargetException | NoSuchMethodException | SecurityException
                        | ClassNotFoundException e) {
                    LOGGER.atWarn().withThrowable(e).log("Failed to register module {}:{}", name::toString,
                            e::getMessage);
                }
            }
        }

        return objectMapper;
    }
}
