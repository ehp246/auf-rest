package me.ehp246.aufrest.api.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;

/**
 * Defines how to bind a response to the return value.
 * <p>
 * Only applicable when the response is a <a href=
 * 'https://developer.mozilla.org/en-US/docs/Web/HTTP/Status#successful_responses'>success</a>.
 *
 * @author Lei Yang
 * @since 4.0
 * @version 4.0
 * @see ByRest
 */
@Retention(RUNTIME)
@Target({ METHOD })
public @interface OfResponse {
    /**
     * Defines what component to return from the response.
     * <p>
     * The default binds the return to the response body.
     * <p>
     * When binding to {@linkplain Bind#HEADER}, the response body will be
     * {@linkplain BodyHandlers#discarding() discarded}.
     *
     * @see {@linkplain Bind}, {@linkplain HttpResponse}
     */
    Bind value() default Bind.BODY;

    /**
     * Defines the header name whose values from the response are to be returned.
     * <p>
     * Only applicable when binding to {@linkplain Bind#HEADER}.
     * <p>
     * The following return types are supported:
     * <li>{@linkplain String}</li>
     * <p>
     * The first value of the header if there is one. Otherwise, <code>null</code>.
     * <li>{@link java.util.List List&lt;String&gt;}</li>
     * <p>
     * All values of the named header if it exists. Otherwise, an empty
     * {@linkplain List}.
     * <li>{@link java.util.Map Map&lt;String, List&lt;String&gt;&gt;}</li>
     * <p>
     * All headers on the response are returned. The name is ignored.
     *
     * @see HttpResponse#headers()
     */
    String header() default "";

    /**
     * Defines how to transform the response body to the return value.
     * <p>
     * Only applicable when binding to {@linkplain Bind#BODY} and the response is a
     * success. Ignored when {@linkplain OfResponse#handler()} is defined.
     */
    BodyOf body() default @BodyOf({});

    /**
     * Defines the components of a HTTP response that can be bound to as return
     * value.
     */
    enum Bind {
        /**
         * @see HttpResponse#headers()
         */
        HEADER,
        /**
         * @see HttpResponse#body()
         */
        BODY
    }

    /**
     * Defines the name of a Spring bean of {@link HttpResponse.BodyHandler} type
     * that would be called to handle the responses on the method.
     * <p>
     * Only applicable when binding to {@linkplain Bind#BODY} and the response is a
     * success.
     * <p>
     * Overwrites {@linkplain OfResponse#body()}.
     *
     * @see HttpClient#send(java.net.http.HttpRequest,
     *      java.net.http.HttpResponse.BodyHandler)
     */
    String handler() default "";

    @Target({})
    @interface BodyOf {
        Class<?>[] value();
    }
}
