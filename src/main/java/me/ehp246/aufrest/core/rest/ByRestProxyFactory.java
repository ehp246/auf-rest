package me.ehp246.aufrest.core.rest;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.slf4j.MDC;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.EnableByRest;
import me.ehp246.aufrest.api.rest.RestFn;
import me.ehp246.aufrest.api.rest.RestFnConfig;
import me.ehp246.aufrest.api.rest.RestFnProvider;
import me.ehp246.aufrest.core.util.OneUtil;

/**
 * Factory of {@linkplain ByRest} beans. Depended by
 * {@linkplain ByRestRegistrar}.
 *
 * @author Lei Yang
 * @see EnableByRest
 * @see ByRestRegistrar
 * @since 1.0
 */
public final class ByRestProxyFactory {
    private final Map<Method, ProxyInvocationBinder> parsedCache = new ConcurrentHashMap<>();

    private final RestFnProvider clientProvider;
    private final ProxyMethodParser methodParser;

    public ByRestProxyFactory(final RestFnProvider restFnProvider, final ProxyMethodParser methodParser) {
        super();
        this.clientProvider = restFnProvider;
        this.methodParser = methodParser;
    }

    @SuppressWarnings("unchecked")
    public <T> T newInstance(final Class<T> byRestInterface) {
        final var byRest = byRestInterface.getAnnotation(ByRest.class);

        return (T) Proxy.newProxyInstance(byRestInterface.getClassLoader(), new Class[] { byRestInterface },
                new InvocationHandler() {
                    private final int hashCode = new Object().hashCode();
                    private final RestFn restFn = clientProvider.get(new RestFnConfig(
                            OneUtil.firstUpper(OneUtil.byRestBeanName(byRestInterface)),
                            Optional.ofNullable(byRest.executor()).map(ByRest.Executor::mdc)
                                    .filter(names -> names.length > 0).map(Arrays::asList).orElseGet(List::of).stream()
                                    .filter(OneUtil::hasValue).collect(Collectors.toMap(String::toString,
                                            name -> ((Supplier<String>) () -> MDC.get(name))))));

                    @Override
                    public Object invoke(final Object proxy, final Method method, final Object[] args)
                            throws Throwable {
                        if (method.getName().equals("toString")) {
                            return byRestInterface.toString();
                        }
                        if (method.getName().equals("hashCode")) {
                            return hashCode;
                        }
                        if (method.getName().equals("equals")) {
                            return proxy == args[0];
                        }

                        if (method.isDefault()) {
                            return MethodHandles.privateLookupIn(byRestInterface, MethodHandles.lookup())
                                    .findSpecial(byRestInterface, method.getName(),
                                            MethodType.methodType(method.getReturnType(), method.getParameterTypes()),
                                            byRestInterface)
                                    .bindTo(proxy).invokeWithArguments(args);
                        }

                        final var bound = parsedCache.computeIfAbsent(method, m -> methodParser.parse(method))
                                .apply(proxy, args);

                        final var outcome = FnOutcome
                                .invoke(() -> restFn.applyForResponse(bound.request(), bound.responseDescriptor()));

                        return bound.returnMapper().apply(bound.request(), outcome);
                    }
                });
    }
}
