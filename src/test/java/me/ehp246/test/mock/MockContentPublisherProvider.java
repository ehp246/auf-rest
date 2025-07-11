package me.ehp246.test.mock;

import java.net.http.HttpRequest.BodyPublishers;

import me.ehp246.aufrest.api.rest.ContentPublisherProvider;
import me.ehp246.aufrest.api.rest.JacksonTypeDescriptor;

/**
 * @author Lei Yang
 *
 */
public class MockContentPublisherProvider implements ContentPublisherProvider {

    @Override
    public <T> ContentPublisher get(final T body, final String mimeType, final JacksonTypeDescriptor descriptor) {
        return new ContentPublisher(mimeType, BodyPublishers.noBody());
    }

}
