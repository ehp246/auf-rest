package me.ehp246.aufrest.core.rest.binder;

import me.ehp246.aufrest.api.rest.JacksonTypeDescriptor;
import me.ehp246.aufrest.core.reflection.ArgBinder;

public class DefaultBodyBinder implements BodyBinder {
    private final String contentType;
    private final ArgBinder<Object, Object> bodyArgBinder;
    private final JacksonTypeDescriptor typeDescriptor;

    public DefaultBodyBinder(String contentType, ArgBinder<Object, Object> bodyArgBinder,
            JacksonTypeDescriptor typeDescriptor) {
        super();
        this.contentType = contentType;
        this.bodyArgBinder = bodyArgBinder;
        this.typeDescriptor = typeDescriptor;
    }

    @Override
    public Bound apply(Object target, Object[] args) throws Throwable {
        return new Bound(bodyArgBinder == null ? null : bodyArgBinder.apply(target, args), typeDescriptor, contentType);
    }

}
