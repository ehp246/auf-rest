package me.ehp246.aufrest.mock;

import java.util.UUID;

import me.ehp246.aufrest.api.rest.Request;

/**
 * @author Lei Yang
 *
 */
public class MockReq implements Request {
	public final String reqId = UUID.randomUUID().toString();

	@Override
	public String uri() {
		return "http://nowhere.com";
	}

}
