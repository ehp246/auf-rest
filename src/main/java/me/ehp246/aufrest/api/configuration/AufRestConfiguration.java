package me.ehp246.aufrest.api.configuration;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandler;
import java.time.Duration;
import java.util.Optional;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import me.ehp246.aufrest.api.rest.AuthProvider;
import me.ehp246.aufrest.api.rest.HeaderProvider;
import me.ehp246.aufrest.api.rest.InvocationAuthProvider;
import me.ehp246.aufrest.api.rest.RequestBuilder;
import me.ehp246.aufrest.api.rest.RestClientConfig;
import me.ehp246.aufrest.api.rest.RestFn;
import me.ehp246.aufrest.api.rest.RestFnProvider;
import me.ehp246.aufrest.api.rest.RestLogger;
import me.ehp246.aufrest.api.spi.BodyHandlerResolver;
import me.ehp246.aufrest.api.spi.InvocationAuthProviderResolver;
import me.ehp246.aufrest.api.spi.PropertyResolver;
import me.ehp246.aufrest.api.spi.ToJson;
import me.ehp246.aufrest.core.util.OneUtil;
import me.ehp246.aufrest.provider.httpclient.DefaultRequestBuilder;
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
@Import({ DefaultRestFnProvider.class, JsonByJackson.class, DefaultBodyHandlerProvider.class })
public final class AufRestConfiguration {
    @Bean
    public RestLogger restLogger(@Value("${" + AufRestConstants.REST_LOGGER + ":false}") final boolean enabled,
            final ToJson toJson) {
        return enabled ? new RestLogger(toJson) : null;
    }

    @Bean
    public RestClientConfig restClientConfig(
            @Value("${" + AufRestConstants.CONNECT_TIMEOUT + ":}") final String connectTimeout) {
        return new RestClientConfig(
                Optional.ofNullable(connectTimeout).filter(OneUtil::hasValue)
                        .map(value -> OneUtil.orThrow(() -> Duration.parse(value),
                                e -> new IllegalArgumentException("Invalid Connection Timeout: " + value)))
                        .orElse(null));
    }

    @Bean
    public PropertyResolver propertyResolver(final org.springframework.core.env.PropertyResolver springResolver) {
        return springResolver::resolveRequiredPlaceholders;
    }

    @Bean
    public RequestBuilder requestBuilder(@Autowired(required = false) final HeaderProvider headerProvider,
            @Autowired(required = false) final AuthProvider authProvider, final ToJson toJson,
            @Value("${" + AufRestConstants.RESPONSE_TIMEOUT + ":}") final String requestTimeout) {
        return new DefaultRequestBuilder(HttpRequest::newBuilder, headerProvider, authProvider, toJson, requestTimeout);
    }

    @Bean
    public InvocationAuthProviderResolver methodAuthProviderMap(final BeanFactory env) {
        return name -> env.getBean(name, InvocationAuthProvider.class);
    }

    @Bean
    public RestFn restFn(final RestFnProvider restFnProvider, final RestClientConfig clientConfig) {
        return restFnProvider.get(clientConfig);
    }

    @Bean
    public BodyHandlerResolver invocationBodyHandlerProvider(final BeanFactory env) {
        return name -> env.getBean(name, BodyHandler.class);
    }
}
