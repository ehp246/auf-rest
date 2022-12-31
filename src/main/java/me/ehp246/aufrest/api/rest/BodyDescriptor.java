package me.ehp246.aufrest.api.rest;

import java.lang.annotation.Annotation;
import java.util.Map;

public interface BodyDescriptor {
    Class<?> type();

    default Map<Class<? extends Annotation>, Annotation> annotations() {
        return null;
    }
}