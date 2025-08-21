package me.ehp246.aufrest.core.rest.binder;

import me.ehp246.aufrest.api.rest.JacksonTypeDescriptor;

/***
 * @author Lei Yang
 */
public interface BodyBinder {
    Bound apply(Object target, Object[] args) throws Throwable;

    record Bound(Object body, JacksonTypeDescriptor bodyDescriptor, String contentType) {
    }
}
