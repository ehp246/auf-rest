package me.ehp246.aufrest.core.rest.binder;

import java.net.http.HttpResponse.BodyHandler;

import me.ehp246.aufrest.api.rest.ResponseHandler;
import me.ehp246.aufrest.core.reflection.ArgBinder;
import me.ehp246.aufrest.core.rest.ProxyReturnMapper;

/**
 * @author Lei Yang
 */
public class DefaultResponseBinder implements ResponseBinder {
    private final ArgBinder<Object, BodyHandler<?>> handlerBinder;
    private final ProxyReturnMapper returnMapper;

    public DefaultResponseBinder(ArgBinder<Object, BodyHandler<?>> handlerBinder, ProxyReturnMapper returnMapper) {
        super();
        this.handlerBinder = handlerBinder;
        this.returnMapper = returnMapper;
    }

    @Override
    public Bound apply(Object target, Object[] args) throws Throwable {
        return new Bound(new ResponseHandler.Provided<>(this.handlerBinder.apply(target, args)), returnMapper);
    }

}
