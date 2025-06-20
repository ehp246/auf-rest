package me.ehp246.aufrest.core.rest;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonView;

import me.ehp246.aufrest.api.annotation.AuthBean;
import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfAuth;
import me.ehp246.aufrest.api.annotation.OfBody;
import me.ehp246.aufrest.api.annotation.OfHeader;
import me.ehp246.aufrest.api.annotation.OfPath;
import me.ehp246.aufrest.api.annotation.OfQuery;
import me.ehp246.aufrest.api.annotation.OfRequest;
import me.ehp246.aufrest.api.annotation.OfResponse;
import me.ehp246.aufrest.api.annotation.OfResponse.Bind;
import me.ehp246.aufrest.api.exception.ProxyInvocationBinderException;
import me.ehp246.aufrest.api.exception.RestFnException;
import me.ehp246.aufrest.api.exception.UnhandledResponseException;
import me.ehp246.aufrest.api.rest.AuthBeanResolver;
import me.ehp246.aufrest.api.rest.BasicAuth;
import me.ehp246.aufrest.api.rest.BearerToken;
import me.ehp246.aufrest.api.rest.BodyHandlerBeanResolver;
import me.ehp246.aufrest.api.rest.BodyHandlerType;
import me.ehp246.aufrest.api.rest.HttpUtils;
import me.ehp246.aufrest.api.rest.InferringBodyHandlerProvider;
import me.ehp246.aufrest.api.rest.JacksonTypeDescriptor;
import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.api.spi.ExpressionResolver;
import me.ehp246.aufrest.core.reflection.ArgBinder;
import me.ehp246.aufrest.core.reflection.ArgBinderProvider;
import me.ehp246.aufrest.core.reflection.ReflectedMethod;
import me.ehp246.aufrest.core.reflection.ReflectedParameter;
import me.ehp246.aufrest.core.reflection.ReflectedType;
import me.ehp246.aufrest.core.util.OneUtil;

/**
 * Parses a proxy method to internal data structure that is ready for turning an
 * invocation to a {@linkplain RestRequest}.
 *
 * @author Lei Yang
 * @see DefaultProxyInvocationBinder
 */
public final class DefaultProxyMethodParser implements ProxyMethodParser {
    private final static Set<Class<? extends Annotation>> PARAMETER_ANNOTATED = Set.of(OfHeader.class, OfPath.class,
            OfQuery.class, OfAuth.class, AuthBean.Param.class);
    private final static Set<Class<?>> PARAMETER_RECOGNIZED = Set.of(BodyPublisher.class, BodyHandler.class);
    private final static ArgBinderProvider<?, ?> ARG_BINDER_PROVIDER = p -> (target, args) -> args[p.index()];

    private final ExpressionResolver expressionResolver;
    private final AuthBeanResolver authBeanResolver;
    private final InferringBodyHandlerProvider inferredHandlerProvider;
    private final BodyHandlerBeanResolver bodyHandlerBeanResolver;

    public DefaultProxyMethodParser(final ExpressionResolver expressionResolver,
            final AuthBeanResolver authBeanResolver, final BodyHandlerBeanResolver bodyHandlerResolver,
            final InferringBodyHandlerProvider inferredHandlerProvider) {
        this.expressionResolver = expressionResolver;
        this.authBeanResolver = authBeanResolver;
        this.inferredHandlerProvider = inferredHandlerProvider;
        this.bodyHandlerBeanResolver = bodyHandlerResolver;
    }

    @Override
    @SuppressWarnings("unchecked")
    public DefaultProxyInvocationBinder parse(final Method method) {
        final var byRest = method.getDeclaringClass().getAnnotation(ByRest.class);
        final var reflected = new ReflectedMethod(method);
        final var ofRequest = reflected.findOnMethod(OfRequest.class);

        final var contentType = ofRequest.map(OfRequest::contentType).filter(OneUtil::hasValue)
                .orElseGet(byRest::contentType);

        final var authHeaders = reflected.allParametersWith(OfAuth.class);
        if (authHeaders.size() > 1) {
            throw new IllegalArgumentException(
                    "Too many " + OfAuth.class.getSimpleName() + " found on " + method.getName());
        }

        final ArgBinder<Object, Supplier<String>> authSupplierFn;
        if (authHeaders.size() == 1) {
            final var param = authHeaders.get(0);
            final var index = param.index();
            if (Supplier.class.isAssignableFrom(param.parameter().getType())) {
                authSupplierFn = (target, args) -> (Supplier<String>) (args[index]);
            } else {
                authSupplierFn = (target, args) -> args[index] == null ? () -> null : args[index]::toString;
            }
        } else {
            authSupplierFn = authSupplierFn(byRest.auth(), reflected);
        }

        /*
         * Body value and publisher Priority: BodyPublisher, @RequestBody, inferred
         *
         * If there is a body publisher on the parameters, take it and ignore everything
         * else.
         */
        final var bodyParam = reflected.findArgumentsOfType(BodyPublisher.class).stream().findFirst()
                .or(reflected.allParametersWith(OfBody.class).stream()::findFirst)
                .or(reflected.filterParametersWith(PARAMETER_ANNOTATED, PARAMETER_RECOGNIZED).stream()::findFirst);

        final var bodyArgBinder = (ArgBinder<Object, Object>) bodyParam.map(ARG_BINDER_PROVIDER::apply).orElse(null);

        final var bodyType = bodyParam.map(ReflectedParameter::parameter)
                .map(parameter -> JacksonTypeDescriptor.of(parameter.getParameterizedType(),
                        Optional.ofNullable(parameter.getAnnotation(JsonView.class)).map(JsonView::value)
                                .filter(OneUtil::hasValue).map(views -> views[0]).orElse(null)))
                .orElse(null);

        return new DefaultProxyInvocationBinder(verb(reflected), accept(byRest, ofRequest), byRest.acceptGZip(),
                contentType, timeout(byRest), baseUrl(byRest, ofRequest), pathParams(reflected), queryParams(reflected),
                queryStatic(byRest), headerParams(reflected), headerStatic(byRest, reflected), authSupplierFn,
                bodyArgBinder, bodyType, responseHandlerBinder(byRest, reflected), proxyReturnMapper(reflected));
    }

    /**
     * Returns and response body handlers are coupled.
     */
    @SuppressWarnings("unchecked")
    private ArgBinder<Object, BodyHandler<?>> responseHandlerBinder(final ByRest byRest,
            final ReflectedMethod reflected) {
        final var ofResponse = reflected.findOnMethod(OfResponse.class);

        // Try argument first.
        final var arg = reflected.findArgumentsOfType(BodyHandler.class).stream().findFirst();
        if (arg.isPresent()) {
            return arg.map(p -> (ArgBinder<Object, BodyHandler<?>>) ARG_BINDER_PROVIDER.apply(p)).get();
        }

        // Named handler bean?
        final var handlerBean = ofResponse.map(OfResponse::handler).filter(OneUtil::hasValue);
        if (handlerBean.isPresent()) {
            return handlerBean.map(bodyHandlerBeanResolver::get)
                    .map(handler -> (ArgBinder<Object, BodyHandler<?>>) (target, args) -> handler).get();
        }

        // Infer from the return type
        final var returnType = reflected.method().getGenericReturnType();
        if (returnType instanceof Class cls && cls.getTypeParameters().length > 0) {
            throw new UnsupportedOperationException("Un-supported return type on " + reflected.method());
        }
        if (returnType == HttpHeaders.class || ofResponse.map(of -> of.value() == Bind.HEADER).orElse(false)) {
            // The headers are wanted, discard the body.
            return (target, args) -> BodyHandlers.discarding();
        }

        Type responseBodyType = returnType;
        if (returnType instanceof ParameterizedType paramType && paramType.getRawType() == HttpResponse.class) {
            responseBodyType = paramType.getActualTypeArguments()[0];
            if (responseBodyType == Void.class) {
                return (target, args) -> BodyHandlers.discarding();
            }
        }

        final var jsonView = reflected.findOnMethod(JsonView.class).map(JsonView::value).filter(OneUtil::hasValue)
                .map(views -> views[0]).orElse(null);

        final var descriptor = new BodyHandlerType.Inferring<>(responseBodyType, jsonView, byRest.errorType());

        final var handler = inferredHandlerProvider.get(descriptor);

        return (target, args) -> handler;
    }

    /**
     * Generates the return mapping function based on the method signature.
     * Exception propagation is implemented here.
     *
     */
    private ProxyReturnMapper proxyReturnMapper(final ReflectedMethod reflected) {
        final Class<?> returnType = reflected.getReturnType();
        final var ofResponse = reflected.findOnMethod(OfResponse.class);
        final var bindToHeader = ofResponse.map(OfResponse::value).map(value -> value == Bind.HEADER).orElse(false);

        // Normal return mapper. Defaults to return the body.
        final Function<HttpResponse<?>, ?> valueMapper;

        /*
         * For this type, no annotation is needed.
         */
        if (returnType.isAssignableFrom(HttpHeaders.class)) {
            valueMapper = HttpResponse::headers;
        } else if (bindToHeader) {
            final var name = ofResponse.get().header();
            if (returnType == String.class) {
                valueMapper = response -> response.headers().firstValue(name).orElse(null);
            } else if (returnType.isAssignableFrom(Map.class)) {
                valueMapper = response -> response.headers().map();
            } else if (returnType.isAssignableFrom(List.class)) {
                valueMapper = response -> response.headers().allValues(name);
            } else {
                throw new IllegalArgumentException("Un-supported return type: " + returnType.toString());
            }
        } else if (returnType.isAssignableFrom(HttpResponse.class)) {
            valueMapper = Function.identity();
        } else if (returnType == void.class && returnType == Void.class) {
            valueMapper = response -> null;
        } else {
            valueMapper = HttpResponse::body;
        }

        /*
         * Compose with Response Exception propagation. This logic applies only when a
         * HttpResponse has been received.
         *
         * An Response Exception based on the status code is always raised first.
         */
        final ProxyReturnMapper mapper = (restReq, outcome) -> {
            final var received = outcome.received();
            /*
             * Was a response received?
             */
            if (received instanceof final HttpResponse<?> httpResponse) {
                /*
                 * Must be a successful response.
                 */
                return valueMapper.apply(httpResponse);
            }

            /*
             * RestFn throws an UnhandledResponseException when a response is received with
             * wrong status code.
             */
            if (received instanceof final UnhandledResponseException unhandledResponse) {
                if (reflected.canThrow(unhandledResponse.getCause())) {
                    throw unhandledResponse.getCause();
                }

                throw unhandledResponse;
            }

            /*
             * Must be an RestFnException.
             */
            if (received instanceof final RestFnException restFnException) {
                final var cause = restFnException.getCause();
                if (cause != null && reflected.canThrow(cause)) {
                    throw cause;
                }
                throw restFnException;
            }

            if (received instanceof final RuntimeException runtime) {
                throw runtime;
            }
            /*
             * What happened? Shouldn't be here.
             */
            throw new RuntimeException("Un-known received: " + received);
        };

        return mapper;
    }

    private Map<Integer, String> headerParams(final ReflectedMethod reflected) {
        return reflected.allParametersWith(OfHeader.class).stream()
                .collect(Collectors.toMap(ReflectedParameter::index, p -> {
                    final var name = Optional.ofNullable(p.parameter().getAnnotation(OfHeader.class).value().toString())
                            .filter(OneUtil::hasValue).orElseGet(() -> p.parameter().getName()).toLowerCase(Locale.US);
                    if (HttpUtils.RESERVED_HEADERS.contains(name)) {
                        throw new IllegalArgumentException("Illegal header '" + name + "' on "
                                + p.parameter().getDeclaringExecutable().toString());
                    }
                    return name;
                }));
    }

    /**
     * Static headers come from annotation on the interface.
     */
    private Map<String, List<String>> headerStatic(final ByRest byRest, final ReflectedMethod reflected) {
        final var headers = Arrays.asList(byRest.headers());
        if ((headers.size() & 1) != 0) {
            throw new IllegalArgumentException("Headers should be in name/value pairs: " + headers);
        }
        final Map<String, List<String>> headerStatic = new HashMap<>();
        for (int i = 0; i < headers.size(); i += 2) {
            final var key = headers.get(i).toLowerCase(Locale.US);
            if (HttpUtils.RESERVED_HEADERS.contains(key.toLowerCase(Locale.US)) || headerStatic.containsKey(key)) {
                throw new IllegalArgumentException("Illegal header '" + headers.get(i) + "' in " + headers + " on "
                        + reflected.method().getDeclaringClass());
            }
            headerStatic.compute(key, (k, v) -> new ArrayList<String>())
                    .add(expressionResolver.resolve(headers.get(i + 1)));
        }
        return headerStatic;
    }

    private Map<String, List<String>> queryStatic(final ByRest byRest) {
        final var queries = Arrays.asList(byRest.queries());
        if ((queries.size() & 1) != 0) {
            throw new IllegalArgumentException("Queries should be in name/value pairs: " + queries);
        }

        final Map<String, List<String>> queryStatic = new HashMap<>();
        for (int i = 0; i < queries.size(); i += 2) {
            queryStatic.computeIfAbsent(queries.get(i), k -> new ArrayList<String>())
                    .add(expressionResolver.resolve(queries.get(i + 1)));
        }
        return queryStatic;
    }

    private Map<Integer, String> queryParams(final ReflectedMethod reflected) {
        return reflected.allParametersWith(OfQuery.class).stream()
                .collect(Collectors.toMap(ReflectedParameter::index, p -> {
                    final var name = p.parameter().getAnnotation(OfQuery.class).value();
                    return OneUtil.hasValue(name) ? name : p.parameter().getName();
                }));
    }

    private Map<String, Integer> pathParams(final ReflectedMethod reflected) {
        return reflected.allParametersWith(OfPath.class).stream().collect(Collectors.toMap(p -> {
            final var name = p.parameter().getAnnotation(OfPath.class).value();
            return OneUtil.hasValue(name) ? name : p.parameter().getName();
        }, ReflectedParameter::index));
    }

    private String accept(final ByRest byRest, final Optional<OfRequest> optionalOfMapping) {
        return optionalOfMapping.map(OfRequest::accept).filter(OneUtil::hasValue).orElseGet(byRest::accept);
    }

    private String verb(final ReflectedMethod reflected) {
        final var method = reflected.method();
        final var optionalOfMapping = reflected.findOnMethod(OfRequest.class);

        return optionalOfMapping.map(OfRequest::method).filter(OneUtil::hasValue)
                .or(HttpUtils.METHOD_NAMES.stream()
                        .filter(name -> method.getName().toUpperCase().startsWith(name))::findAny)
                .map(String::toUpperCase)
                .orElseThrow(() -> new IllegalArgumentException("Un-defined HTTP method on " + method.toString()));
    }

    private Duration timeout(final ByRest byRest) {
        return Optional.ofNullable(byRest.timeout()).filter(OneUtil::hasValue).map(expressionResolver::resolve)
                .map(text -> OneUtil.orThrow(() -> Duration.parse(text),
                        e -> new IllegalArgumentException("Invalid timeout: " + text, e)))
                .orElse(null);
    }

    /**
     * Parses the base URL from the method and the interface. Resolves any property
     * place holders.
     * <p>
     * Empty string in case everything is missing/blank.
     */
    private String baseUrl(final ByRest byRest, final Optional<OfRequest> optionalOfMapping) {
        return expressionResolver
                .resolve(byRest.value() + optionalOfMapping.map(OfRequest::value).filter(OneUtil::hasValue).orElse(""));
    }

    private ArgBinder<Object, Supplier<String>> authSupplierFn(final ByRest.Auth auth,
            final ReflectedMethod reflected) {
        final var value = List.of(auth.value());
        switch (auth.scheme()) {
        case SIMPLE:
            if (value.size() < 1) {
                throw new IllegalArgumentException("Missing required arguments for " + auth.scheme() + " on "
                        + reflected.method().getDeclaringClass());
            }
            final var simple = expressionResolver.resolve(value.get(0));
            return (target, args) -> simple::toString;
        case BASIC:
            if (value.size() < 2) {
                throw new IllegalArgumentException("Missing required arguments for " + auth.scheme() + " on "
                        + reflected.method().getDeclaringClass());
            }
            final var basic = new BasicAuth(expressionResolver.resolve(value.get(0)),
                    expressionResolver.resolve(value.get(1)));
            return (target, args) -> basic::header;
        case BEARER:
            if (value.size() < 1) {
                throw new IllegalArgumentException("Missing required arguments for " + auth.scheme() + " on "
                        + reflected.method().getDeclaringClass());
            }
            final var bearer = new BearerToken(expressionResolver.resolve(value.get(0)));
            return (target, args) -> bearer::header;
        case BEAN:
            if (value.size() < 2) {
                throw new IllegalArgumentException("Missing required arguments for " + auth.scheme() + " on "
                        + reflected.method().getDeclaringClass());
            }
            final var beanName = value.get(0);
            final var methodName = value.get(1);
            final var bean = authBeanResolver.get(beanName);
            final var beanParams = reflected.allParametersWith(AuthBean.Param.class);
            final var reflectedType = new ReflectedType<>(bean.getClass());
            final var method = reflectedType.streamMethodsWith(AuthBean.Invoking.class)
                    .filter(m -> Optional.ofNullable(m.getAnnotation(AuthBean.Invoking.class).value())
                            .filter(OneUtil::hasValue).orElseGet(m::getName).equals(methodName))
                    .findFirst().or(
                            () -> reflectedType
                                    .findMethod(methodName,
                                            beanParams.stream().map(ReflectedParameter::parameter)
                                                    .map(Parameter::getType).toList().toArray(new Class<?>[] {})))
                    .get();
            final var beanArgs = new Object[beanParams.size()];

            return (target, args) -> {
                for (int i = 0; i < beanArgs.length; i++) {
                    beanArgs[i] = args[beanParams.get(i).index()];
                }

                final String header;
                try {
                    header = OneUtil.toString(method.invoke(bean, beanArgs));
                } catch (final InvocationTargetException e) {
                    final var t = e.getTargetException();
                    if (reflected.canThrow(t)) {
                        throw t;
                    }
                    throw new ProxyInvocationBinderException(t);
                } catch (final Throwable e) {
                    if (reflected.canThrow(e)) {
                        throw e;
                    }
                    throw new ProxyInvocationBinderException(e);
                }

                // Header could be null.
                return () -> header;
            };
        case NONE:
            return (target, args) -> () -> null;
        default:
            return (target, args) -> null;
        }
    }
}