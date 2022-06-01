package me.ehp246.aufrest.core.byrest;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpResponse.BodyHandler;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import me.ehp246.aufrest.api.annotation.AuthHeader;
import me.ehp246.aufrest.api.annotation.OfMapping;
import me.ehp246.aufrest.api.rest.ByRestProxyConfig;
import me.ehp246.aufrest.api.rest.HttpUtils;
import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.api.spi.PropertyResolver;
import me.ehp246.aufrest.core.reflection.ReflectedProxyMethod;
import me.ehp246.aufrest.core.util.OneUtil;

/**
 * @author Lei Yang
 *
 */
final class MethodParsingRequestBuilder {
    private final static Set<Class<? extends Annotation>> PARAMETER_ANNOTATED = Set.of(PathVariable.class,
            RequestParam.class, RequestHeader.class, AuthHeader.class);
    private final static Set<Class<?>> PARAMETER_RECOGNIZED = Set.of(BodyPublisher.class, BodyHandler.class);

    private final ReflectedProxyMethod reflected;
    private final ByRestProxyConfig config;
    private final String method;
    private final UriComponentsBuilder uriBuilder;
    private final Map<String, Integer> pathMap = new HashMap<>();

    MethodParsingRequestBuilder(final Method method, final ByRestProxyConfig proxyConfig,
            final PropertyResolver propertyResolver) {
        this.reflected = new ReflectedProxyMethod(method);
        this.config = proxyConfig;

        final var optionalOfMapping = reflected.findOnMethod(OfMapping.class);

        this.method = optionalOfMapping.map(OfMapping::method).filter(OneUtil::hasValue)
                .or(() -> HttpUtils.METHOD_NAMES.stream()
                        .filter(name -> method.getName().toUpperCase().startsWith(name)).findAny())
                .map(String::toUpperCase)
                .orElseThrow(() -> new IllegalArgumentException("Un-defined HTTP method on " + method.toString()));

        this.uriBuilder = UriComponentsBuilder.fromUriString(propertyResolver.resolve(
                proxyConfig.uri() + optionalOfMapping.map(OfMapping::value).filter(OneUtil::hasValue).orElse("")));

        this.reflected.allParametersWith(PathVariable.class).forEach(p -> {
            this.pathMap.put(p.parameter().getAnnotation(PathVariable.class).value(), p.index());
        });
    }

    public RestRequest apply(final Object[] args) {
        final var pathArgs = new HashMap<String, Object>();
        this.pathMap.entrySet().stream().forEach(entry -> {
            final var arg = args[entry.getValue()];
            if (arg instanceof Map<?, ?> map) {
                map.entrySet().stream().forEach(e -> pathArgs.putIfAbsent(e.getKey().toString(),
                        UriUtils.encode(e.getValue().toString(), StandardCharsets.UTF_8)));
            } else {
                pathArgs.put(entry.getKey(), UriUtils.encode(arg.toString(), StandardCharsets.UTF_8));
            }
        });

        final var uri = this.uriBuilder.buildAndExpand(pathArgs).toUriString();

        return new RestRequest() {

            @Override
            public String method() {
                return method;
            }

            @Override
            public String uri() {
                return uri;
            }

        };
    }
}
