package me.ehp246.aufrest.core.rest;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import me.ehp246.aufrest.api.annotation.AuthBean;
import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfAuth;
import me.ehp246.aufrest.api.annotation.OfBody;
import me.ehp246.aufrest.api.annotation.OfHeader;
import me.ehp246.aufrest.api.annotation.OfMapping;
import me.ehp246.aufrest.api.annotation.OfPath;
import me.ehp246.aufrest.api.annotation.OfQuery;
import me.ehp246.aufrest.api.exception.BadGatewayException;
import me.ehp246.aufrest.api.exception.ClientErrorResponseException;
import me.ehp246.aufrest.api.exception.ErrorResponseException;
import me.ehp246.aufrest.api.exception.GatewayTimeoutException;
import me.ehp246.aufrest.api.exception.InternalServerErrorException;
import me.ehp246.aufrest.api.exception.RedirectionResponseException;
import me.ehp246.aufrest.api.exception.ServerErrorResponseException;
import me.ehp246.aufrest.api.exception.ServiceUnavailableException;
import me.ehp246.aufrest.api.exception.UnhandledResponseException;
import me.ehp246.aufrest.api.rest.AuthBeanResolver;
import me.ehp246.aufrest.api.rest.BasicAuth;
import me.ehp246.aufrest.api.rest.BearerToken;
import me.ehp246.aufrest.api.rest.BodyDescriptor.JsonViewValue;
import me.ehp246.aufrest.api.rest.BodyDescriptor.ReturnValue;
import me.ehp246.aufrest.api.rest.BodyHandlerBeanResolver;
import me.ehp246.aufrest.api.rest.BodyHandlerProvider;
import me.ehp246.aufrest.api.rest.HttpUtils;
import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.api.spi.PropertyResolver;
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
 * @see DefaultInvocationRequestBinder
 */
public final class DefaultProxyMethodParser implements ProxyMethodParser {
    private final static Set<Class<? extends Annotation>> PARAMETER_ANNOTATED = Set.of(OfHeader.class, OfPath.class,
            OfQuery.class, OfAuth.class, AuthBean.Param.class);
    private final static Set<Class<?>> PARAMETER_RECOGNIZED = Set.of(BodyPublisher.class, BodyHandler.class);
    private final static ArgBinderProvider<?, ?> ARG_BINDER_PROVIDER = p -> (target, args) -> args[p.index()];

    private final PropertyResolver propertyResolver;
    private final AuthBeanResolver authBeanResolver;
    private final BodyHandlerProvider jsonBodyHandlerProvider;
    private final BodyHandlerBeanResolver bodyHandlerResolver;

    public DefaultProxyMethodParser(final PropertyResolver propertyResolver, final AuthBeanResolver authBeanResolver,
            final BodyHandlerBeanResolver bodyHandlerResolver, final BodyHandlerProvider jsonBodyHandlerProvider) {
        this.propertyResolver = propertyResolver;
        this.authBeanResolver = authBeanResolver;
        this.jsonBodyHandlerProvider = jsonBodyHandlerProvider;
        this.bodyHandlerResolver = bodyHandlerResolver;
    }

    @Override
    @SuppressWarnings("unchecked")
    public DefaultInvocationRequestBinder parse(final Method method) {
        final var byRest = method.getDeclaringClass().getAnnotation(ByRest.class);
        final var reflected = new ReflectedMethod(method);
        final var optionalOfMapping = reflected.findOnMethod(OfMapping.class);

        final var contentType = optionalOfMapping.map(OfMapping::contentType).filter(OneUtil::hasValue)
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

        final var bodyInfo = bodyParam.map(p -> {
            final var parameter = p.parameter();
            return new JsonViewValue(parameter.getType(), parameter.getAnnotations());
        }).orElse(null);

        /*
         * Returns and response body handlers are coupled.
         */
        final var returnType = reflected.getReturnType();
        /**
         * Priority: BodyHandler parameter, @OfMapping named, ByRestProxyConfig,
         * built-in recognized types, default BindingBodyHandlerProvider
         */
        final var consumerBinder = reflected.findArgumentsOfType(BodyHandler.class).stream().findFirst()
                .map(p -> (ArgBinder<Object, BodyHandler<?>>) ARG_BINDER_PROVIDER.apply(p))
                .or(() -> optionalOfMapping.map(OfMapping::consumerHandler).filter(OneUtil::hasValue)
                        .map(bodyHandlerResolver::get).map(handler -> (target, args) -> handler))
                .or(() -> Optional.ofNullable(byRest.consumerHandler()).filter(OneUtil::hasValue)
                        .map(bodyHandlerResolver::get).map(handler -> (target, args) -> handler))
                .orElseGet(() -> {
                    final var descriptor = new ReturnValue(returnType, byRest.errorType(),
                            reflected.method().getDeclaredAnnotations());
                    if (descriptor.type().isAssignableFrom(HttpResponse.class) && descriptor.reifying() == null) {
                        throw new IllegalArgumentException("Missing required " + OfBody.class);
                    }
                    final var handler = jsonBodyHandlerProvider.get(descriptor);
                    return (target, args) -> handler;
                });

        return new DefaultInvocationRequestBinder(verb(reflected), accept(byRest, optionalOfMapping), byRest.acceptGZip(), contentType,
                timeout(byRest), baseUrl(byRest, optionalOfMapping), pathParams(reflected), queryParams(reflected), queryStatic(byRest), headerParams(reflected),
                headerStatic(byRest, reflected), authSupplierFn, bodyArgBinder, bodyInfo, consumerBinder, returnMapper(reflected));
    }

    private Map<Integer, String> headerParams(final ReflectedMethod reflected) {
        final var headerParams = reflected.allParametersWith(OfHeader.class).stream().map(p -> {
            final var name = p.parameter().getAnnotation(OfHeader.class).value();
            if (HttpUtils.RESERVED_HEADERS.contains(name.toLowerCase(Locale.US))) {
                throw new IllegalArgumentException(
                        "Illegal header '" + name + "' on " + p.parameter().getDeclaringExecutable().toString());
            }
            return p;
        }).collect(Collectors.toMap(ReflectedParameter::index,
                p -> p.parameter().getAnnotation(OfHeader.class).value().toString().toLowerCase(Locale.US)));

        final var namesOnParam = headerParams.values();
        if (namesOnParam.size() > new HashSet<String>(namesOnParam).size()) {
            throw new IllegalArgumentException("Duplicate header names on " + reflected.method());
        }
        return headerParams;
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
                    .add(propertyResolver.resolve(headers.get(i + 1)));
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
                    .add(propertyResolver.resolve(queries.get(i + 1)));
        }
        return queryStatic;
    }

    private Map<Integer, String> queryParams(final ReflectedMethod reflected) {
        return reflected.allParametersWith(OfQuery.class).stream().collect(
                Collectors.toMap(ReflectedParameter::index, p -> p.parameter().getAnnotation(OfQuery.class).value()));
    }

    private Map<String, Integer> pathParams(final ReflectedMethod reflected) {
        return reflected.allParametersWith(OfPath.class).stream().collect(
                Collectors.toMap(p -> p.parameter().getAnnotation(OfPath.class).value(), ReflectedParameter::index));
    }

    private String accept(final ByRest byRest, final Optional<OfMapping> optionalOfMapping) {
        return optionalOfMapping.map(OfMapping::accept).filter(OneUtil::hasValue).orElseGet(byRest::accept);
    }

    private String verb(final ReflectedMethod reflected) {
        final var method = reflected.method();
        final var optionalOfMapping = reflected.findOnMethod(OfMapping.class);

        return optionalOfMapping.map(OfMapping::method).filter(OneUtil::hasValue)
                .or(HttpUtils.METHOD_NAMES.stream()
                        .filter(name -> method.getName().toUpperCase().startsWith(name))::findAny)
                .map(String::toUpperCase)
                .orElseThrow(() -> new IllegalArgumentException("Un-defined HTTP method on " + method.toString()));
    }

    private Duration timeout(final ByRest byRest) {
        return Optional.ofNullable(byRest.timeout()).filter(OneUtil::hasValue).map(propertyResolver::resolve)
                .map(text -> OneUtil.orThrow(() -> Duration.parse(text),
                        e -> new IllegalArgumentException("Invalid timeout: " + text, e)))
                .orElse(null);
    }

    /**
     * Generates the return mapping function based on the method signature.
     * Exception propagation is implemented here.
     *
     */
    private ResponseReturnMapper returnMapper(final ReflectedMethod reflected) {
        final Class<?> returnType = reflected.getReturnType();
        final OfHeader ofHeader = reflected.findOnMethod(OfHeader.class).orElse(null);

        // Normal return mapper. Defaults to return the body.
        final Function<HttpResponse<?>, ?> valueMapper;

        if (returnType.isAssignableFrom(HttpHeaders.class)) {
            valueMapper = HttpResponse::headers;
        } else if (ofHeader != null) {
            final var name = ofHeader.value();
            if (returnType == String.class) {
                valueMapper = response -> response.headers().firstValue(name).orElse(null);
            } else if (returnType.isAssignableFrom(Map.class)) {
                valueMapper = response -> response.headers().map();
            } else if (returnType.isAssignableFrom(List.class)) {
                valueMapper = response -> response.headers().allValues(name);
            } else {
                throw new IllegalArgumentException(
                        OfHeader.class.toString() + " does not support return type: " + returnType.toString());
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
        final ResponseReturnMapper mapper = (restReq, httpResponse) -> {
            // Should throw the more specific type if possible.
            ErrorResponseException ex = null;
            final var statusCode = httpResponse.statusCode();
            if (statusCode >= 600) {
                ex = new ErrorResponseException(restReq, httpResponse);
            } else if (statusCode == 500) {
                ex = new InternalServerErrorException(restReq, httpResponse);
            } else if (statusCode == 502) {
                ex = new BadGatewayException(restReq, httpResponse);
            } else if (statusCode == 503) {
                ex = new ServiceUnavailableException(restReq, httpResponse);
            } else if (statusCode == 504) {
                ex = new GatewayTimeoutException(restReq, httpResponse);
            } else if (statusCode >= 500) {
                ex = new ServerErrorResponseException(restReq, httpResponse);
            } else if (statusCode >= 400) {
                ex = new ClientErrorResponseException(restReq, httpResponse);
            } else if (statusCode >= 300) {
                ex = new RedirectionResponseException(restReq, httpResponse);
            }

            if (ex != null) {
                if (reflected.isOnThrows(ex.getClass())) {
                    throw ex;
                }

                throw new UnhandledResponseException(ex);
            }

            return valueMapper.apply(httpResponse);
        };

        return mapper;
    }

    /**
     * Parses the base URL from the method and the interface. Resolves any property
     * place holders.
     * <p>
     * Empty string in case everything is missing/blank.
     */
    private String baseUrl(final ByRest byRest, final Optional<OfMapping> optionalOfMapping) {
        return propertyResolver
                .resolve(byRest.value() + optionalOfMapping.map(OfMapping::value).filter(OneUtil::hasValue).orElse(""));
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
            final var simple = propertyResolver.resolve(value.get(0));
            return (target, args) -> simple::toString;
        case BASIC:
            if (value.size() < 2) {
                throw new IllegalArgumentException("Missing required arguments for " + auth.scheme() + " on "
                        + reflected.method().getDeclaringClass());
            }
            final var basic = new BasicAuth(propertyResolver.resolve(value.get(0)),
                    propertyResolver.resolve(value.get(1)));
            return (target, args) -> basic::header;
        case BEARER:
            if (value.size() < 1) {
                throw new IllegalArgumentException("Missing required arguments for " + auth.scheme() + " on "
                        + reflected.method().getDeclaringClass());
            }
            final var bearer = new BearerToken(propertyResolver.resolve(value.get(0)));
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
            final var reflectedType = new ReflectedType(bean.getClass());
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
                } catch (final Throwable e) {
                    throw e instanceof final RuntimeException re ? re : new RuntimeException(e);
                }

                return () -> header;
            };
        case NONE:
            return (target, args) -> () -> null;
        default:
            return (target, args) -> null;
        }
    }
}