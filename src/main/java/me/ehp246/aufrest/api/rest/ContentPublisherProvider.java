package me.ehp246.aufrest.api.rest;

import java.net.http.HttpRequest.BodyPublisher;

/**
 * @author Lei Yang
 * @since 4.0
 */
public interface ContentPublisherProvider {
    /**
     * Returns a {@linkplain BodyPublisher} for the payload object.
     * <p>
     * Doesn't support form encoded queries.
     *
     * @param <T>
     * @param body
     * @param mimeType   Desired media type for the out-going request. Can be
     *                   <code>null</code>. In which case,
     *                   {@linkplain ContentPublisher#contentType} will be inferred
     *                   on the Java type of the <code>body</code>. Defaults to
     *                   <code>application/json</code>.
     * @param descriptor
     * @return
     */
    <T> ContentPublisher get(T body, String mimeType, RestBodyDescriptor<T> descriptor);

    default <T> ContentPublisher get(final T body, final RestBodyDescriptor<T> descriptor) {
        return this.get(body, null, descriptor);
    }
    /**
     * {@linkplain ContentPublisher#contentType()} defines HTTP header
     * <code>content-type</code> on the out-going request. It can be different from
     * {@linkplain RestRequest#contentType()}.
     */
    record ContentPublisher(String contentType, BodyPublisher publisher) {
    }
}
