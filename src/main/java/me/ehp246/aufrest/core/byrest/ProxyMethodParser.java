package me.ehp246.aufrest.core.byrest;

import java.lang.reflect.Method;

import me.ehp246.aufrest.api.rest.ByRestProxyConfig;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface ProxyMethodParser {
    ProxyToRestFn parse(Method method, ByRestProxyConfig proxyConfig);
}
