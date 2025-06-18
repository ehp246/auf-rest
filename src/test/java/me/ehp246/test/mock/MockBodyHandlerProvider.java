package me.ehp246.test.mock;

import java.net.http.HttpResponse.BodyHandler;

import me.ehp246.aufrest.api.rest.BodyHandlerType;
import me.ehp246.aufrest.api.rest.InferringBodyHandlerProvider;

/**
 * @author Lei Yang
 *
 */
public class MockBodyHandlerProvider implements InferringBodyHandlerProvider {

    @Override
    public <T> BodyHandler<T> get(BodyHandlerType descriptor) {
        // TODO Auto-generated method stub
        return null;
    }

}
