package me.ehp246.aufrest.api.rest;

import java.net.http.HttpResponse.BodyHandler;
import java.util.Map;

import me.ehp246.aufrest.api.annotation.ByRest;

/**
 * The abstraction that provides a {@linkplain BodyHandler} given a
 * {@linkplain ResponseHandler.Inferring} object which typically comes from a
 * {@linkplain ByRest} method return signature.
 * <p>
 * Available as a Spring bean at runtime.
 *
 * @author Lei Yang
 * @since 4.0
 */
@FunctionalInterface
public interface InferringBodyHandlerProvider {
    /**
     * Returns a {@linkplain BodyHandler handler} that can process both success and
     * error body.
     * <p>
     * Can be <code>null</code>. In which case, the response body will be
     * transformed on a best-effort approach based on the <code>content-type</code>
     * header. For JSON types, it could be de-serialized to primitive types or
     * {@linkplain Map Map&lt;String, Object;&gt;}. Other text types will be raw
     * {@linkplain String}.
     *
     */
    <T> BodyHandler<T> get(ResponseHandler.Inferring inferring);
}
