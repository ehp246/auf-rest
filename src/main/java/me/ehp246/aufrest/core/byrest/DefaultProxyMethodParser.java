package me.ehp246.aufrest.core.byrest;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import me.ehp246.aufrest.api.annotation.AuthBean;
import me.ehp246.aufrest.api.annotation.AuthHeader;
import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfMapping;
import me.ehp246.aufrest.api.annotation.Reifying;
import me.ehp246.aufrest.api.rest.AuthBeanResolver;
import me.ehp246.aufrest.api.rest.BasicAuth;
import me.ehp246.aufrest.api.rest.BearerToken;
import me.ehp246.aufrest.api.rest.BindingBodyHandlerProvider;
import me.ehp246.aufrest.api.rest.BindingDescriptor;
import me.ehp246.aufrest.api.rest.HttpUtils;
import me.ehp246.aufrest.api.rest.RestRequest.BodyAs;
import me.ehp246.aufrest.api.spi.BodyHandlerResolver;
import me.ehp246.aufrest.api.spi.PropertyResolver;
import me.ehp246.aufrest.core.reflection.ReflectedMethod;
import me.ehp246.aufrest.core.reflection.ReflectedParameter;
import me.ehp246.aufrest.core.reflection.ReflectedType;
import me.ehp246.aufrest.core.util.OneUtil;

/**
 * @author Lei Yang
 *
 */
public final class DefaultProxyMethodParser implements ProxyMethodParser {
    private final static Set<Class<? extends Annotation>> PARAMETER_ANNOTATED = Set.of(PathVariable.class,
            RequestParam.class, RequestHeader.class, AuthHeader.class, AuthBean.Param.class);
    private final static Set<Class<?>> PARAMETER_RECOGNIZED = Set.of(BodyPublisher.class, BodyHandler.class);

    private final PropertyResolver propertyResolver;
    private final AuthBeanResolver authBeanResolver;
    private final BindingBodyHandlerProvider bindingBodyHandlerProvider;
    private final BodyHandlerResolver bodyHandlerResolver;

    public DefaultProxyMethodParser(final PropertyResolver propertyResolver, final AuthBeanResolver authBeanResolver,
            final BodyHandlerResolver bodyHandlerResolver,
            final BindingBodyHandlerProvider bindingBodyHandlerProvider) {
        this.propertyResolver = propertyResolver;
        this.authBeanResolver = authBeanResolver;
        this.bindingBodyHandlerProvider = bindingBodyHandlerProvider;
        this.bodyHandlerResolver = bodyHandlerResolver;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ReflectedInvocationRequestBuilder parse(final Method method) {
        final var byRest = method.getDeclaringClass().getAnnotation(ByRest.class);

        final var reflected = new ReflectedMethod(method);
        final var optionalOfMapping = reflected.findOnMethod(OfMapping.class);

        final var verb = optionalOfMapping.map(OfMapping::method).filter(OneUtil::hasValue)
                .or(HttpUtils.METHOD_NAMES.stream()
                        .filter(name -> method.getName().toUpperCase().startsWith(name))::findAny)
                .map(String::toUpperCase)
                .orElseThrow(() -> new IllegalArgumentException("Un-defined HTTP method on " + method.toString()));

        final var uriBuilder = UriComponentsBuilder.fromUriString(propertyResolver.resolve(
                byRest.value() + optionalOfMapping.map(OfMapping::value).filter(OneUtil::hasValue).orElse("")));

        final var pathParams = reflected.allParametersWith(PathVariable.class).stream().collect(Collectors
                .toMap(p -> p.parameter().getAnnotation(PathVariable.class).value(), ReflectedParameter::index));

        final var queryParams = reflected.allParametersWith(RequestParam.class).stream().collect(Collectors
                .toMap(ReflectedParameter::index, p -> p.parameter().getAnnotation(RequestParam.class).value()));

        /*
         * Parameter headers
         */
        final var headerParams = reflected.allParametersWith(RequestHeader.class).stream().map(p -> {
            final var name = p.parameter().getAnnotation(RequestHeader.class).value();
            if (HttpUtils.RESERVED_HEADERS.contains(name.toLowerCase(Locale.US))) {
                throw new IllegalArgumentException(
                        "Illegal header '" + name + "' on " + p.parameter().getDeclaringExecutable().toString());
            }
            return p;
        }).collect(Collectors.toMap(ReflectedParameter::index,
                p -> p.parameter().getAnnotation(RequestHeader.class).value().toString()));

        /*
         * Static headers
         */
        final var headers = Arrays.asList(byRest.headers());
        if ((headers.size() & 1) != 0) {
            throw new IllegalArgumentException("Headers should be name/value pair: " + headers);
        }
        final Map<String, List<String>> headerStatic = new HashMap<>();
        for (int i = 0; i < headers.size(); i += 2) {
            final var key = headers.get(i);
            if (HttpUtils.RESERVED_HEADERS.contains(key.toLowerCase(Locale.US))) {
                throw new IllegalArgumentException(
                        "Illegal header '" + key + "' in " + headers + " on " + reflected.method().getDeclaringClass());
            }
            final var value = new ArrayList<String>();
            value.add(propertyResolver.resolve(headers.get(i + 1)));
            headerStatic.merge(key, value, (o, n) -> {
                o.addAll(n);
                return o;
            });
        }

        final var accept = optionalOfMapping.map(OfMapping::accept).filter(OneUtil::hasValue).orElse(byRest.accept());

        final var contentType = optionalOfMapping.map(OfMapping::contentType).filter(OneUtil::hasValue)
                .or(() -> Optional.ofNullable(byRest.contentType())).filter(OneUtil::hasValue)
                .orElse(HttpUtils.APPLICATION_JSON);

        final var authHeaders = reflected.allParametersWith(AuthHeader.class);
        if (authHeaders.size() > 1) {
            throw new IllegalArgumentException(
                    "Too many " + AuthHeader.class.getSimpleName() + " found on " + method.getName());
        }

        final BiFunction<Object, Object[], Supplier<?>> authSupplierFn;
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

        /**
         * Priority: BodyHandler parameter, @OfMapping named, ByRestProxyConfig, default
         * BindingBodyHandlerProvider
         */
        final var bodyHandlerFn = reflected.findArgumentsOfType(BodyHandler.class).stream().findFirst()
                .map(p -> (BiFunction<Object, Object[], BodyHandler<?>>) (target,
                        args) -> (BodyHandler<?>) (args[p.index()]))
                .or(() -> optionalOfMapping.map(OfMapping::responseBodyHandler).filter(OneUtil::hasValue)
                        .map(bodyHandlerResolver::get).map(handler -> (target, args) -> handler))
                .or(() -> Optional.ofNullable(byRest.responseBodyHandler()).filter(OneUtil::hasValue)
                        .map(bodyHandlerResolver::get).map(handler -> (target, args) -> handler))
                .orElseGet(() -> {
                    final var bodyHandler = bindingBodyHandlerProvider.get(bindingOf(reflected, byRest));
                    return (target, args) -> bodyHandler;
                });

        /*
         * Priority: BodyPublisher, @RequestBody, inferred
         */
        final var bodyParam = reflected.findArgumentsOfType(BodyPublisher.class).stream().findFirst()
                .or(reflected.allParametersWith(RequestBody.class).stream()::findFirst)
                .or(reflected.filterParametersWith(PARAMETER_ANNOTATED, PARAMETER_RECOGNIZED).stream()::findFirst);

        final var bodyFn = bodyParam.map(p -> (BiFunction<Object, Object[], Object>) (target, args) -> args[p.index()])
                .orElse(null);
        final var bodyAs = bodyParam.map(p -> (BodyAs) p.parameter()::getType).orElse(null);

        final var timeout = Optional.ofNullable(byRest.timeout()).filter(OneUtil::hasValue)
                .map(propertyResolver::resolve).map(text -> OneUtil.orThrow(() -> Duration.parse(text),
                        e -> new IllegalArgumentException("Invalid timeout: " + text, e)))
                .orElse(null);

        return new ReflectedInvocationRequestBuilder(verb, accept, byRest.acceptGZip(), contentType, timeout,
                uriBuilder, pathParams, queryParams, headerParams, headerStatic, authSupplierFn, bodyHandlerFn, bodyFn,
                bodyAs);
    }

    private BiFunction<Object, Object[], Supplier<?>> authSupplierFn(final ByRest.Auth auth,
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
                } catch (Throwable e) {
                    throw e instanceof RuntimeException re ? re : new RuntimeException(e);
                }

                return () -> header;
            };
        case NONE:
            return (target, args) -> () -> null;
        default:
            return (target, args) -> null;
        }
    }

    private static BindingDescriptor bindingOf(final ReflectedMethod method, final ByRest byRest) {
        final var returnTypes = returnTypes(Stream
                .concat(Arrays.stream(new Class<?>[] { method.getReturnType() }),
                        Arrays.stream(
                                method.getMethodValueOf(Reifying.class, Reifying::value, () -> new Class<?>[] {})))
                .collect(Collectors.toList()));

        return new BindingDescriptor(returnTypes.get(0), byRest.errorType(),
                returnTypes.size() == 0 ? List.of() : returnTypes.subList(1, returnTypes.size()),
                method.getMethodDeclaredAnnotations());
    }

    private static List<Class<?>> returnTypes(final List<Class<?>> types) {
        if (types.size() == 0) {
            throw new IllegalArgumentException("Missing required " + Reifying.class.getName());
        }

        final var head = types.get(0);
        if (head.isAssignableFrom(HttpResponse.class) || head.isAssignableFrom(CompletableFuture.class)) {
            return returnTypes(new ArrayList<>(types.subList(1, types.size())));
        }
        return types;
    }
}