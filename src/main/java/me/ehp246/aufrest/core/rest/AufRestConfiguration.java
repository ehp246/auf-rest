package me.ehp246.aufrest.core.rest;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandler;
import java.time.Duration;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.ehp246.aufrest.api.configuration.AufRestConstants;
import me.ehp246.aufrest.api.configuration.ObjectMapperConfiguration;
import me.ehp246.aufrest.api.rest.AuthBeanResolver;
import me.ehp246.aufrest.api.rest.AuthProvider;
import me.ehp246.aufrest.api.rest.BodyHandlerBeanResolver;
import me.ehp246.aufrest.api.rest.ClientConfig;
import me.ehp246.aufrest.api.rest.ContentPublisherProvider;
import me.ehp246.aufrest.api.rest.HeaderProvider;
import me.ehp246.aufrest.api.rest.HttpClientBuilderSupplier;
import me.ehp246.aufrest.api.rest.RestFn;
import me.ehp246.aufrest.api.rest.RestFnProvider;
import me.ehp246.aufrest.api.rest.RestLogger;
import me.ehp246.aufrest.api.spi.PropertyResolver;
import me.ehp246.aufrest.core.util.OneUtil;
import me.ehp246.aufrest.provider.httpclient.DefaultHttpRequestBuilder;
import me.ehp246.aufrest.provider.httpclient.DefaultRestFnProvider;
import me.ehp246.aufrest.provider.jackson.JsonByObjectMapper;

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
        ObjectMapperConfiguration.class })
public final class AufRestConfiguration implements BeanDefinitionRegistryPostProcessor {
    @Bean("3eddc6a6-f990-4f41-b6e5-2ae1f931dde7")
    public RestLogger restLogger(@Value("${" + AufRestConstants.REST_LOGGER_ENABLED + ":false}") final boolean enabled,
            @Value("${" + AufRestConstants.REST_LOGGER_MASKED + ":authorization}") final Set<String> masked) {
        return enabled ? new RestLogger(masked) : null;
    }

    @Bean("8d4bb36b-67e6-4af9-8d27-c69ed217e235")
    public ClientConfig clientConfig(
            @Value("${" + AufRestConstants.CONNECT_TIMEOUT + ":}") final String connectTimeout) {
        return new ClientConfig(
                Optional.ofNullable(connectTimeout).filter(OneUtil::hasValue)
                        .map(value -> OneUtil.orThrow(() -> Duration.parse(value),
                                e -> new IllegalArgumentException("Invalid Connection Timeout: " + value)))
                        .orElse(null));
    }

    @Bean("55b212a8-2783-4a46-aa5d-60ceb4b2c0d9")
    public PropertyResolver propertyResolver(final org.springframework.core.env.PropertyResolver springResolver) {
        return springResolver::resolveRequiredPlaceholders;
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
    public AuthBeanResolver methodAuthProviderMap(final BeanFactory env) {
        return name -> env.getBean(name);
    }

    @Bean("ac6621d6-1220-4248-ba3f-29f9dc54499b")
    public RestFn restFn(final RestFnProvider restFnProvider, final ClientConfig clientConfig) {
        return restFnProvider.get(clientConfig);
    }

    @Bean("216fbb62-0701-43fb-9fdd-a6df279c92bc")
    public BodyHandlerBeanResolver invocationBodyHandlerProvider(final BeanFactory env) {
        return name -> env.getBean(name, BodyHandler.class);
    }

    @Bean("404d421e-45a8-483e-9f62-2367cfda4a80")
    public HttpClientBuilderSupplier httpClientBuilderSupplier() {
        return HttpClient::newBuilder;
    }

    @Bean
    public JsonByObjectMapper jsonByObjectMapper(
            @Qualifier(AufRestConstants.BEAN_OBJECT_MAPPER) final ObjectMapper objectMapper) {
        return new JsonByObjectMapper(objectMapper);
    }

    @Override
    public void postProcessBeanDefinitionRegistry(final BeanDefinitionRegistry registry) throws BeansException {
        if (registry.containsBeanDefinition(AufRestConstants.BEAN_OBJECT_MAPPER)) {
            return;
        }

        if (registry.containsBeanDefinition("objectMapper")) {
            registry.registerBeanDefinition(AufRestConstants.BEAN_OBJECT_MAPPER,
                    registry.getBeanDefinition("objectMapper"));
            return;
        }

        final var beanDef = new GenericBeanDefinition();
        beanDef.setBeanClass(ObjectMapper.class);
        beanDef.setFactoryBeanName(ObjectMapperConfiguration.class.getName());
        beanDef.setFactoryMethodName(AufRestConstants.BEAN_OBJECT_MAPPER);

        registry.registerBeanDefinition(AufRestConstants.BEAN_OBJECT_MAPPER, beanDef);
    }

    @Override
    public void postProcessBeanFactory(final ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }
}
