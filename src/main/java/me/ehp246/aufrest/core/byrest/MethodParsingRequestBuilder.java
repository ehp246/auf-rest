package me.ehp246.aufrest.core.byrest;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpResponse.BodyHandler;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpHeaders;
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
    private final String accept;
    private final String contentType;
    private final UriComponentsBuilder uriBuilder;
    private final Map<String, Integer> pathMap = new HashMap<>();
    private final Map<Integer, String> queryMap = new HashMap<>();
    private final Map<String, List<String>> defaultHeaders = new HashMap<>();

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

        this.reflected.allParametersWith(RequestParam.class).forEach(p -> {
            this.queryMap.put(p.index(), p.parameter().getAnnotation(RequestParam.class).value());
        });

        // Set accept-encoding at a lower priority.
        if (proxyConfig.acceptGZip()) {
            defaultHeaders.put(HttpHeaders.ACCEPT_ENCODING.toLowerCase(Locale.US), List.of("gzip"));
        }

        this.accept = optionalOfMapping.map(OfMapping::accept).filter(OneUtil::hasValue).orElse(proxyConfig.accept());
        this.contentType = optionalOfMapping.map(OfMapping::contentType).filter(OneUtil::hasValue)
                .orElse(proxyConfig.contentType());
        ;
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

        final var queryParams = new HashMap<String, List<String>>();
        this.queryMap.entrySet().forEach(entry -> {
            final var arg = args[entry.getKey()];
            if (arg instanceof Map<?, ?> map) {
                map.entrySet().stream().forEach(e -> queryParams.merge(e.getKey().toString(),
                        new ArrayList<>(Arrays.asList(OneUtil.toString(e.getValue()))), (o, p) -> {
                            o.add(p.get(0));
                            return o;
                        }));
            } else if (arg instanceof List<?> list) {
                list.stream().forEach(v -> queryParams.merge(entry.getValue(),
                        new ArrayList<>(Arrays.asList(OneUtil.toString(v))), (o, p) -> {
                            o.add(p.get(0));
                            return o;
                        }));
            } else {
                queryParams.merge(entry.getValue(), new ArrayList<>(Arrays.asList(OneUtil.toString(arg))), (o, p) -> {
                    o.add(p.get(0));
                    return o;
                });
            }
        });

        final var headers = new HashMap<String, List<String>>(defaultHeaders);

        return new RestRequest() {

            @Override
            public String method() {
                return method;
            }

            @Override
            public String uri() {
                return uri;
            }

            @Override
            public Map<String, List<String>> queryParams() {
                return queryParams;
            }

            @Override
            public Map<String, List<String>> headers() {
                return headers;
            }

            @Override
            public String contentType() {
                return contentType;
            }

            @Override
            public String accept() {
                return accept;
            }

        };
    }
}
