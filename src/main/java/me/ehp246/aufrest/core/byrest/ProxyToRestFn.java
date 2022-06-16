package me.ehp246.aufrest.core.byrest;

import me.ehp246.aufrest.api.rest.RestRequest;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
interface ProxyToRestFn {
    RestRequest apply(Object target, Object[] args);
}