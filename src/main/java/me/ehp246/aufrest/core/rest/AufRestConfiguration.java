package me.ehp246.aufrest.core.rest;

import java.lang.reflect.InvocationTargetException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandler;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.expression.StandardBeanExpressionResolver;
import org.springframework.util.ClassUtils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import me.ehp246.aufrest.api.configuration.AufRestConstants;
import me.ehp246.aufrest.api.rest.AuthBeanResolver;
import me.ehp246.aufrest.api.rest.AuthProvider;
import me.ehp246.aufrest.api.rest.BodyHandlerBeanResolver;
import me.ehp246.aufrest.api.rest.ContentPublisherProvider;
import me.ehp246.aufrest.api.rest.HeaderProvider;
import me.ehp246.aufrest.api.rest.HttpClientBuilderSupplier;
import me.ehp246.aufrest.api.rest.RestFn;
import me.ehp246.aufrest.api.rest.RestFnConfig;
import me.ehp246.aufrest.api.rest.RestFnProvider;
import me.ehp246.aufrest.api.rest.RestLogger;
import me.ehp246.aufrest.api.spi.ExpressionResolver;
import me.ehp246.aufrest.core.util.OneUtil;
import me.ehp246.aufrest.provider.httpclient.DefaultHttpRequestBuilder;
import me.ehp246.aufrest.provider.httpclient.DefaultRestFnProvider;
import me.ehp246.aufrest.provider.jackson.JsonByJackson;

/**
 * Registers infrastructure beans needed by the framework.
 *
 * <p>
 * Imported by {@link me.ehp246.aufrest.api.annotation.EnableByRest
 * EnableByRest}.
 *
 * @author Lei Yang
 * @see me.ehp246.aufrest.api.annotation.EnableByRest
 * @since 1.0
 */
@Import({ DefaultRestFnProvider.class, DefaultInferringBodyHandlerProvider.class, DefaultContentPublisherProvider.class,
        DefaultHttpClientExecutorProvider.class })
public final class AufRestConfiguration {
    private final static List<String> MODULES = List.of("com.fasterxml.jackson.datatype.jsr310.JavaTimeModule",
            "com.fasterxml.jackson.module.mrbean.MrBeanModule",
            "com.fasterxml.jackson.module.paramnames.ParameterNamesModule");

    @Bean("3eddc6a6-f990-4f41-b6e5-2ae1f931dde7")
    public RestLogger restLogger(@Value("${" + AufRestConstants.REST_LOGGER_ENABLED + ":false}") final boolean enabled,
            @Value("${" + AufRestConstants.REST_LOGGER_MASKED + ":authorization}") final Set<String> masked) {
        return enabled ? new RestLogger(masked) : null;
    }

    @Bean("55b212a8-2783-4a46-aa5d-60ceb4b2c0d9")
    public ExpressionResolver expressionResolver(
            final org.springframework.core.env.PropertyResolver springPropertyResolver,
            final ConfigurableBeanFactory beanFactory) {
        final var springSpelResolver = new StandardBeanExpressionResolver();
        final var context = new BeanExpressionContext(beanFactory, null);
        return exp -> {
            if (exp.startsWith("#{")) {
                return springSpelResolver.evaluate(exp, context).toString();
            }
            return springPropertyResolver.resolveRequiredPlaceholders(exp);
        };
    }

    @Bean("baa8af0b-4da4-487f-a686-3d1e8387dbb6")
    public HttpRequestBuilder requestBuilder(@Autowired(required = false) final HeaderProvider headerProvider,
            @Autowired(required = false) final AuthProvider authProvider,
            final ContentPublisherProvider publisherProvider,
            @Value("${" + AufRestConstants.RESPONSE_TIMEOUT + ":}") final String requestTimeout) {
        return new DefaultHttpRequestBuilder(publisherProvider, HttpRequest::newBuilder, headerProvider, authProvider,
                requestTimeout);
    }

    @Bean("8a7808c6-d088-42e5-a504-ab3dad149e1d")
    public AuthBeanResolver authBeanResolver(final BeanFactory env) {
        return name -> env.getBean(name);
    }

    @Bean("ac6621d6-1220-4248-ba3f-29f9dc54499b")
    public RestFn restFn(final RestFnProvider restFnProvider) {
        return restFnProvider.get(new RestFnConfig("ac6621d6-1220-4248-ba3f-29f9dc54499b"));
    }

    @Bean("216fbb62-0701-43fb-9fdd-a6df279c92bc")
    public BodyHandlerBeanResolver bodyHandlerBeanResolver(final BeanFactory env) {
        return name -> env.getBean(name, BodyHandler.class);
    }

    @Bean("404d421e-45a8-483e-9f62-2367cfda4a80")
    public HttpClientBuilderSupplier httpClientBuilderProvider(
            @Value("${" + AufRestConstants.CONNECT_TIMEOUT + ":}") final String connectTimeout) {
        final var conTimeout = Optional.ofNullable(connectTimeout).filter(OneUtil::hasValue)
                .map(value -> OneUtil.orThrow(() -> Duration.parse(value),
                        e -> new IllegalArgumentException("Invalid Connection Timeout: " + value)))
                .orElse(null);
        return () -> {
            final var newBuilder = HttpClient.newBuilder();
            if (conTimeout != null) {
                newBuilder.connectTimeout(conTimeout);
            }
            return newBuilder;
        };
    }

    @Bean("96eb8fd6-602c-4f61-8621-f29f70365be5")
    public JsonByJackson jsonByObjectMapper(final ApplicationContext appCtx) {
        final var aufRestObjectMapper = appCtx.getBeansOfType(ObjectMapper.class)
                .get(AufRestConstants.AUF_REST_OBJECT_MAPPER);
        if (aufRestObjectMapper != null) {
            return new JsonByJackson(aufRestObjectMapper);
        }

        try {
            return new JsonByJackson(appCtx.getBean(ObjectMapper.class));
        } catch (final Exception e) {
            // Can not find a default. Creating private.
        }

        final ObjectMapper newMapper = new ObjectMapper().setSerializationInclusion(Include.NON_NULL)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        for (final var name : MODULES) {
            if (ClassUtils.isPresent(name, this.getClass().getClassLoader())) {
                try {
                    newMapper.registerModule((Module) Class.forName(name).getDeclaredConstructor((Class[]) null)
                            .newInstance((Object[]) null));
                } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                        | InvocationTargetException | NoSuchMethodException | SecurityException
                        | ClassNotFoundException e) {
                }
            }
        }

        return new JsonByJackson(newMapper);
    }
}
