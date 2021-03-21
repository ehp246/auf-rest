package me.ehp246.aufrest.mock;

import java.util.UUID;

import me.ehp246.aufrest.api.rest.ProxyByRest;
import me.ehp246.aufrest.api.rest.ReqByRest;

/**
 * @author Lei Yang
 *
 */
public class MockReq implements ReqByRest {
	public final String reqId = UUID.randomUUID().toString();

	@Override
	public String uri() {
		return "http://nowhere.com";
	}

	@Override
	public ProxyByRest invokedOn() {
		return null;
	}

}
