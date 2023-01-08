package me.ehp246.test.mock;

import java.net.http.HttpResponse.BodyHandler;

import me.ehp246.aufrest.api.rest.InferringBodyHandlerProvider;
import me.ehp246.aufrest.api.rest.RestResponseDescriptor;

/**
 * @author Lei Yang
 *
 */
public class MockBodyHandlerProvider implements InferringBodyHandlerProvider {

    @Override
    public <T> BodyHandler<T> get(RestResponseDescriptor<T> descriptor) {
        // TODO Auto-generated method stub
        return null;
    }

}
