package me.ehp246.aufrest.api.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;

/**
 * @author Lei Yang
 * @since 4.0
 * @version 4.0
 * @see ByRest
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface OfResponse {
    /**
     * Defines the name of a Spring bean of {@link HttpResponse.BodyHandler} type
     * that would be called to handle the responses on the method.
     *
     * @see HttpClient#send(java.net.http.HttpRequest,
     *      java.net.http.HttpResponse.BodyHandler)
     */
    String handler() default "";
}
