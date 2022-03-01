package me.ehp246.aufrest.core.byrest;

import java.lang.annotation.Annotation;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import me.ehp246.aufrest.api.annotation.AuthHeader;
import me.ehp246.aufrest.api.annotation.OfMapping;
import me.ehp246.aufrest.api.annotation.Reifying;
import me.ehp246.aufrest.api.rest.BasicAuth;
import me.ehp246.aufrest.api.rest.BearerToken;
import me.ehp246.aufrest.api.rest.BodyReceiver;
import me.ehp246.aufrest.api.rest.ByRestProxyConfig;
import me.ehp246.aufrest.api.rest.HttpUtils;
import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.api.spi.InvocationAuthProviderResolver;
import me.ehp246.aufrest.api.spi.PropertyResolver;
import me.ehp246.aufrest.core.reflection.AnnotatedArgument;
import me.ehp246.aufrest.core.reflection.ProxyInvocation;
import me.ehp246.aufrest.core.util.OneUtil;

/**
 * @author Lei Yang
 *
 */
final class RestRequestFromInvocation {

    private final static Set<Class<? extends Annotation>> PARAMETER_ANNOTATIONS = Set.of(PathVariable.class,
            RequestParam.class, RequestHeader.class, AuthHeader.class);

    private final Optional<Supplier<String>> proxyAuthSupplier;
    private final InvocationAuthProviderResolver methodAuthProviderMap;
    private final ByRestProxyConfig byRestConfig;
    private final PropertyResolver propertyResolver;
    private final Duration timeout;

    RestRequestFromInvocation(final ByRestProxyConfig byRestConfig,
            final InvocationAuthProviderResolver methodAuthProviderMap, final PropertyResolver propertyResolver) {
        super();
        this.byRestConfig = byRestConfig;
        this.methodAuthProviderMap = methodAuthProviderMap;
        this.propertyResolver = propertyResolver;
        this.proxyAuthSupplier = Optional.of(byRestConfig.auth()).map(auth -> {
            switch (auth.scheme()) {
            case SIMPLE:
                if (auth.value().size() < 1) {
                    throw new IllegalArgumentException("Missing required arguments for " + auth.scheme().name());
                }
                return propertyResolver.resolve(auth.value().get(0))::toString;
            case BASIC:
                if (auth.value().size() < 2) {
                    throw new IllegalArgumentException("Missing required arguments for " + auth.scheme().name());
                }
                return new BasicAuth(propertyResolver.resolve(auth.value().get(0)),
                        propertyResolver.resolve(auth.value().get(1)))::value;
            case BEARER:
                if (auth.value().size() < 1) {
                    throw new IllegalArgumentException("Missing required arguments for " + auth.scheme().name());
                }
                return new BearerToken(propertyResolver.resolve(auth.value().get(0)))::value;
            case NONE:
                return () -> null;
            default:
                return null;
            }
        });
        this.timeout = Optional.ofNullable(byRestConfig.timeout()).filter(OneUtil::hasValue)
                .map(propertyResolver::resolve).map(text -> OneUtil.orThrow(() -> Duration.parse(text),
                        e -> new IllegalArgumentException("Invalid Timeout: " + text, e)))
                .orElse(null);
    }

    /**
     * Creates a {@link RestRequest} from a {@link ProxyInvocation}.
     * 
     */
    @SuppressWarnings("unchecked")
    RestRequest from(ProxyInvocation invocation) {
        final var optionalOfMapping = invocation.findOnMethod(OfMapping.class);

        final var pathParams = invocation.mapAnnotatedArguments(PathVariable.class, PathVariable::value).entrySet()
                .stream().map(entry -> {
                    if (entry.getKey().equals("")) {
                        return entry;
                    }
                    entry.setValue(UriUtils.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
                    return entry;
                }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        final var unnamedPathMap = pathParams.get("");
        if (unnamedPathMap != null && unnamedPathMap instanceof Map) {
            ((Map<String, Object>) unnamedPathMap).entrySet().stream().forEach(entry -> pathParams
                    .putIfAbsent(entry.getKey(), UriUtils.encode(entry.getValue().toString(), StandardCharsets.UTF_8)));
        }

        final var queryParamArgs = invocation.<String, Object, RequestParam>mapAnnotatedArguments(RequestParam.class,
                RequestParam::value);

        final var unnamedQueryMap = queryParamArgs.get("");

        if (unnamedQueryMap != null && unnamedQueryMap instanceof Map) {
            queryParamArgs.remove("");
            ((Map<String, Object>) unnamedQueryMap).entrySet().stream()
                    .forEach(e -> queryParamArgs.putIfAbsent(e.getKey(), e.getValue()));
        }

        final var id = UUID.randomUUID().toString();
        final var queryParams = queryParamArgs.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(),
                e -> e.getValue() == null ? List.<String>of() : List.of(e.getValue().toString())));
        final var uri = UriComponentsBuilder
                .fromUriString(propertyResolver.resolve(this.byRestConfig.uri()
                        + optionalOfMapping.map(OfMapping::value).filter(OneUtil::hasValue).orElse("")))
                .buildAndExpand(pathParams).toUriString();

        final var method = optionalOfMapping.map(OfMapping::method).filter(OneUtil::hasValue).or(() -> {
            final var invokedMethodName = invocation.getMethodName().toUpperCase();
            return HttpUtils.METHOD_NAMES.stream().filter(name -> invokedMethodName.startsWith(name)).findAny();
        }).map(String::toUpperCase).orElseThrow(() -> new RuntimeException("Un-defined HTTP method"));

        final var accept = optionalOfMapping.map(OfMapping::accept).orElse(this.byRestConfig.accept());

        final var payload = invocation.filterPayloadArgs(PARAMETER_ANNOTATIONS);

        final var headers = new HashMap<String, List<String>>();

        // Set accept-encoding at a lower priority.
        if (byRestConfig.acceptGZip()) {
            headers.put(HttpHeaders.ACCEPT_ENCODING.toLowerCase(Locale.US), List.of("gzip"));
        }

        // Collect headers from invocation arguments. Last step, highest priority.
        invocation.streamOfAnnotatedArguments(RequestHeader.class)
                .forEach(new Consumer<AnnotatedArgument<RequestHeader>>() {
                    @Override
                    public void accept(final AnnotatedArgument<RequestHeader> annoArg) {
                        newValue(annoArg.getAnnotation().value(), annoArg.getArgument());
                    }

                    private void newValue(final Object key, final Object newValue) {
                        if (newValue == null) {
                            return;
                        }

                        if (newValue instanceof Iterable<?> iter) {
                            iter.forEach(v -> newValue(key, v));
                            return;
                        }

                        if (newValue instanceof Map<?, ?> map) {
                            map.entrySet().forEach(entry -> {
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
                .concat(Arrays.stream(new Class<?>[] { invocation.getReturnType() }),
                        Arrays.stream(
                                invocation.getMethodValueOf(Reifying.class, Reifying::value, () -> new Class<?>[] {})))
                .collect(Collectors.toList()));

        final var bodyReceiver = new BodyReceiver() {

            @Override
            public Class<?> type() {
                return returnTypes.get(0);
            }

            @Override
            public Class<?> errorType() {
                return byRestConfig.errorType();
            }

            @Override
            public List<Class<?>> reifying() {
                return returnTypes.size() == 0 ? List.of() : returnTypes.subList(1, returnTypes.size());
            }

            @Override
            public List<? extends Annotation> annotations() {
                return invocation.getMethodDeclaredAnnotations();
            }
        };

        final var authSupplier = invocation.streamOfAnnotatedArguments(AuthHeader.class).findFirst()
                .map(arg -> (Supplier<String>) () -> OneUtil.toString(arg.getArgument()))
                .orElse(optionalOfMapping.map(OfMapping::authProvider).filter(OneUtil::hasValue)
                        .map(name -> (Supplier<String>) () -> methodAuthProviderMap.get(name).get(invocation))
                        .orElse(proxyAuthSupplier.orElse(null)));

        final var body = payload.size() >= 1 ? payload.get(0) : null;

        final var contentType = Optional.ofNullable(optionalOfMapping.map(OfMapping::contentType)
                .filter(OneUtil::hasValue).orElseGet(this.byRestConfig::contentType)).filter(OneUtil::hasValue)
                .orElseGet(() -> {
                    // TODO: Determine content type by the body object type.
                    // Defaults to JSON.
                    return HttpUtils.APPLICATION_JSON;
                });

        return new RestRequest() {

            @Override
            public String id() {
                return id;
            }

            @Override
            public String uri() {
                return uri;
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

            @Override
            public Map<String, List<String>> queryParams() {
                return queryParams;
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
