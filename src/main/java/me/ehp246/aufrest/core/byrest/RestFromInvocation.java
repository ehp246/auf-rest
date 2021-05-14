package me.ehp246.aufrest.core.byrest;

import java.lang.annotation.Annotation;
import java.net.URLEncoder;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import me.ehp246.aufrest.api.annotation.AuthHeader;
import me.ehp246.aufrest.api.annotation.OfMapping;
import me.ehp246.aufrest.api.annotation.Reifying;
import me.ehp246.aufrest.api.rest.BodyReceiver;
import me.ehp246.aufrest.api.rest.HttpUtils;
import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.api.spi.InvocationAuthProviderResolver;
import me.ehp246.aufrest.core.reflection.AnnotatedArgument;
import me.ehp246.aufrest.core.reflection.ProxyInvoked;
import me.ehp246.aufrest.core.util.OneUtil;

/**
 * @author Lei Yang
 *
 */
final class RestFromInvocation {
    private final static Set<Class<? extends Annotation>> PARAMETER_ANNOTATIONS = Set.of(PathVariable.class,
            RequestParam.class, RequestHeader.class, AuthHeader.class);

    private final Function<String, String> uriResolver;
    private final Duration timeout;
    private final Optional<Supplier<String>> proxyAuthSupplier;
    private final String contentType;
    private final String accept;
    private final InvocationAuthProviderResolver methodAuthProviderMap;

    RestFromInvocation(final Function<String, String> uriResolver, final InvocationAuthProviderResolver methodAuthProviderMap,
            final Duration timeout, final Optional<Supplier<String>> proxyAuthSupplier, final String contentType,
            final String accept) {
        super();
        this.uriResolver = uriResolver;
        this.timeout = timeout;
        this.proxyAuthSupplier = proxyAuthSupplier;
        this.contentType = contentType;
        this.accept = accept;
        this.methodAuthProviderMap = methodAuthProviderMap;
    }

    @SuppressWarnings("unchecked")
    RestRequest get(ProxyInvoked invoked) {
        final var ofMapping = invoked.findOnMethod(OfMapping.class);

        final var pathParams = invoked.mapAnnotatedArguments(PathVariable.class, PathVariable::value);
        final var unnamedPathMap = pathParams.get("");

        if (unnamedPathMap != null && unnamedPathMap instanceof Map) {
            ((Map<String, Object>) unnamedPathMap).entrySet().stream()
                    .forEach(entry -> pathParams.putIfAbsent(entry.getKey(), entry.getValue()));
        }

        final var queryParams = invoked.mapAnnotatedArguments(RequestParam.class, RequestParam::value);
        final var unnamedQueryMap = queryParams.get("");

        if (unnamedQueryMap != null && unnamedQueryMap instanceof Map) {
            queryParams.remove("");
            ((Map<String, Object>) unnamedQueryMap).entrySet().stream()
                    .forEach(e -> queryParams.putIfAbsent(e.getKey(), e.getValue()));
        }

        final String id = UUID.randomUUID().toString();
        final String url = UriComponentsBuilder
                .fromUriString(uriResolver.apply(ofMapping.map(OfMapping::value).filter(OneUtil::hasValue).orElse("")))
                .queryParams(CollectionUtils.toMultiValueMap(queryParams.entrySet().stream()
                        .collect(Collectors.toMap(e -> OneUtil.orThrow(() -> URLEncoder.encode(e.getKey(), "UTF-8")),
                                e -> OneUtil
                                        .orThrow(() -> List.of(URLEncoder.encode(e.getValue().toString(), "UTF-8")))))))
                .buildAndExpand(pathParams).toUriString();

        final String method = ofMapping.map(OfMapping::method).filter(OneUtil::hasValue).or(() -> {
            final var invokedMethodName = invoked.getMethodName().toUpperCase();
            return HttpUtils.METHOD_NAMES.stream().filter(name -> invokedMethodName.startsWith(name)).findAny();
        }).map(String::toUpperCase).orElseThrow(() -> new RuntimeException("Un-defined HTTP method"));

        final var accept = ofMapping.map(OfMapping::accept).orElse(this.accept);

        final var contentType = ofMapping.map(OfMapping::contentType).orElse(this.contentType);

        final var payload = invoked.filterPayloadArgs(PARAMETER_ANNOTATIONS);

        final var headers = new HashMap<String, List<String>>();

        invoked.streamOfAnnotatedArguments(RequestHeader.class)
                .forEach(new Consumer<AnnotatedArgument<RequestHeader>>() {
                    @Override
                    public void accept(final AnnotatedArgument<RequestHeader> annoArg) {
                        newValue(annoArg.getAnnotation().value(), annoArg.getArgument());
                    }

                    private void newValue(final Object key, final Object newValue) {
                        if (newValue == null) {
                            return;
                        }

                        if (newValue instanceof Iterable) {
                            ((Iterable<Object>) newValue).forEach(v -> newValue(key, v));
                            return;
                        }

                        if (newValue instanceof Map) {
                            ((Map<Object, Object>) newValue).entrySet().forEach(entry -> {
                                newValue(entry.getKey(), entry.getValue());
                            });
                            return;
                        }

                        getMapped(key).add(newValue.toString());
                    }

                    private List<String> getMapped(final Object key) {
                        return headers.computeIfAbsent(key.toString(), k -> new ArrayList<String>());
                    }
                });

        final var returnTypes = bodyType(Stream
                .concat(Arrays.stream(new Class<?>[] { invoked.getReturnType() }),
                        Arrays.stream(
                                invoked.getMethodValueOf(Reifying.class, Reifying::value, () -> new Class<?>[] {})))
                .collect(Collectors.toList()));

        final var bodyReceiver = new BodyReceiver() {

            @Override
            public Class<?> type() {
                return returnTypes.get(0);
            }

            @Override
            public List<Class<?>> reifying() {
                return returnTypes.size() == 0 ? List.of() : returnTypes.subList(1, returnTypes.size());
            }

            @Override
            public List<? extends Annotation> annotations() {
                return invoked.getMethodDeclaredAnnotations();
            }
        };

        final var authSupplier = invoked.streamOfAnnotatedArguments(AuthHeader.class).findFirst()
                .map(arg -> (Supplier<String>) () -> OneUtil.toString(arg.getArgument()))
                .orElse(ofMapping.map(OfMapping::authProvider).filter(OneUtil::hasValue)
                        .map(name -> (Supplier<String>) () -> methodAuthProviderMap.get(name).get(invoked))
                        .orElse(proxyAuthSupplier.orElse(null)));

        final var body = payload.size() >= 1 ? payload.get(0) : null;

        return new RestRequest() {

            @Override
            public String id() {
                return id;
            }

            @Override
            public String uri() {
                return url;
            }

            @Override
            public String method() {
                return method;
            }

            @Override
            public Duration timeout() {
                return timeout;
            }

            @Override
            public Supplier<String> authSupplier() {
                return authSupplier;
            }

            @Override
            public String contentType() {
                return contentType;
            }

            @Override
            public String accept() {
                return accept;
            }

            @Override
            public BodyReceiver bodyReceiver() {
                return bodyReceiver;
            }

            @Override
            public Object body() {
                return body;
            }

            @Override
            public Map<String, List<String>> headers() {
                return headers;
            }
        };
    }

    private static List<Class<?>> bodyType(final List<Class<?>> types) {
        if (types.size() == 0) {
            throw new IllegalArgumentException("Missing required " + Reifying.class.getName());
        }

        final var head = types.get(0);
        if (head.isAssignableFrom(HttpResponse.class) || head.isAssignableFrom(CompletableFuture.class)) {
            return bodyType(new ArrayList<>(types.subList(1, types.size())));
        }
        return types;
    }
}
