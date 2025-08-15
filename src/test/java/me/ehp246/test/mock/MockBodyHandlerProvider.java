package me.ehp246.test.mock;

import java.net.http.HttpResponse.BodyHandler;

import me.ehp246.aufrest.api.rest.InferringBodyHandlerProvider;
import me.ehp246.aufrest.api.rest.ResponseHandler;

/**
 * @author Lei Yang
 *
 */
public class MockBodyHandlerProvider implements InferringBodyHandlerProvider {

    @Override
    public <T> BodyHandler<T> get(ResponseHandler.Inferring descriptor) {
        return null;
    }

}
