package me.ehp246.aufrest.core.rest;

import java.net.http.HttpResponse.BodyHandler;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import me.ehp246.aufrest.api.rest.JacksonTypeDescriptor;
import me.ehp246.aufrest.api.rest.ResponseHandler;
import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.core.reflection.ArgBinder;
import me.ehp246.aufrest.core.rest.binder.BodyBinder;
import me.ehp246.aufrest.core.rest.binder.HeaderBinder;
import me.ehp246.aufrest.core.rest.binder.PathBinder;
import me.ehp246.aufrest.core.rest.binder.QueryBinder;

/**
 * Builds a {@linkplain RestRequest} from an invocation on a parsed proxy
 * method.
 *
 * @author Lei Yang
 * @see DefaultProxyMethodParser
 * @since 4.0
 */
final class DefaultProxyInvocationBinder implements ProxyInvocationBinder {
    private final String method;
    private final PathBinder pathBinder;
    private final Duration timeout;
    private final BodyBinder bodyBinder;
    private final QueryBinder queryBinder;
    private final HeaderBinder headerBinder;
    // Response body
    private final ArgBinder<Object, BodyHandler<?>> handlerBinder;
    private final ProxyReturnMapper returnMapper;

    DefaultProxyInvocationBinder(final String method, final Duration timeout, final PathBinder pathBinder,
            final QueryBinder queryBinder, final HeaderBinder headerBinder, final BodyBinder bodyBinder,
            final ArgBinder<Object, BodyHandler<?>> consumerBinder, final ProxyReturnMapper returnMapper) {
        super();
        this.method = method;
        this.pathBinder = pathBinder;
        this.timeout = timeout;
        this.bodyBinder = bodyBinder;
        this.queryBinder = queryBinder;
        this.headerBinder = headerBinder;
        this.handlerBinder = consumerBinder;
        this.returnMapper = returnMapper;
    }

    @Override
    public Bound apply(final Object target, final Object[] args) throws Throwable {
        final var boundPath = this.pathBinder.apply(target, args);

        final var boundQuery = this.queryBinder.aapply(target, args);

        final var boundHeaders = this.headerBinder.apply(target, args);

        final var boundBody = this.bodyBinder.apply(target, args);

        final var id = UUID.randomUUID().toString();

        return new Bound(new RestRequest() {

            @Override
            public String id() {
                return id;
            }

            @Override
            public String method() {
                return method;
            }

            @Override
            public String uri() {
                return boundPath.baseUrl();
            }

            @Override
            public Map<String, Object> paths() {
                return boundPath.paths();
            }

            @Override
            public Map<String, List<String>> queries() {
                return boundQuery;
            }

            @Override
            public Map<String, List<String>> headers() {
                return boundHeaders.headers();
            }

            @Override
            public String contentType() {
                return boundBody.contentType();
            }

            @Override
            public String accept() {
                return boundHeaders.accept();
            }

            @Override
            public String acceptEncoding() {
                return boundHeaders.acceptEncoding();
            }

            @Override
            public Supplier<String> authSupplier() {
                return boundHeaders.authSupplier();
            }

            @Override
            public Duration timeout() {
                return timeout;
            }

            @Override
            public Object body() {
                return boundBody.body();
            }

            @Override
            public JacksonTypeDescriptor bodyDescriptor() {
                return boundBody.bodyDescriptor();
            }

        }, new ResponseHandler.Provided<>(handlerBinder.apply(target, args)), returnMapper);
    }
}
