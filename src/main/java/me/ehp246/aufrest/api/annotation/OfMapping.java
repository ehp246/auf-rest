package me.ehp246.aufrest.api.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;

/**
 * Instructs the library on how to construct HTTP requests from method
 * invocations.
 * <p>
 * It is to be applied to methods on
 * {@link me.ehp246.aufrest.api.annotation.ByRest ByRest} interfaces.
 *
 * @author Lei Yang
 * @since 1.0
 * @see ByRest
 */
@Retention(RUNTIME)
@Target({ METHOD })
public @interface OfMapping {
    /**
     * Defines additional path to append to the URL specified by
     * {@link me.ehp246.aufrest.api.annotation.ByRest ByRest} on the interface.
     * <p>
     * Simple path only without query parameters.
     * <p>
     * Spring property place-holders are supported. Simple string concatenation to
     * construct the final URL.
     *
     * @see <a href=
     *      "https://developer.mozilla.org/en-US/docs/Learn/Common_questions/What_is_a_URL#:~:text=With%20Hypertext%20and%20HTTP%2C%20URL,unique%20resource%20on%20the%20Web.">URL</a>
     */
    String value() default "";

    /**
     * Defines HTTP method for the request.
     * <p>
     * Empty string indicates to derive HTTP method from interface method prefix.
     * <p>
     * Specified string is changed to upper case before sent. There is no validation
     * on the specified value.
     */
    String method() default "";

    /**
     * Defines the content type and appropriate serialization provider.
     */
    String contentType() default "";

    /**
     * Defines the Accept header. Usually derived by the return type of the method.
     */
    String accept() default "application/json";

    /**
     * Defines the name of a Spring bean of {@link HttpResponse.BodyHandler} type
     * that would be called to handle the response for the method.
     *
     * @return a bean name
     * @see HttpClient#send(java.net.http.HttpRequest,
     *      java.net.http.HttpResponse.BodyHandler)
     */
    String consumerHandler() default "";
}
