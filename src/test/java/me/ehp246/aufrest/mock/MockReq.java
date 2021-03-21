package me.ehp246.aufrest.mock;

import java.util.UUID;

import me.ehp246.aufrest.api.rest.InvokedOn;
import me.ehp246.aufrest.api.rest.RequestByRest;

/**
 * @author Lei Yang
 *
 */
public class MockReq implements RequestByRest {
	public final String reqId = UUID.randomUUID().toString();

	@Override
	public String uri() {
		return "http://nowhere.com";
	}

	@Override
	public InvokedOn invokedOn() {
		return null;
	}

}
