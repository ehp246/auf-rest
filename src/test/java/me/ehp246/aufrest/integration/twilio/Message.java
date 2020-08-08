package me.ehp246.aufrest.integration.twilio;

import java.net.http.HttpResponse;

import org.springframework.web.bind.annotation.RequestParam;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.ByRest.Auth;
import me.ehp246.aufrest.api.annotation.ByRest.Auth.Type;
import me.ehp246.aufrest.api.annotation.OfMapping;
import me.ehp246.aufrest.api.rest.HttpUtils;

/**
 * @author Lei Yang
 *
 */
@ByRest(value = "${ApiRoot}/Accounts/${AccountSid}/Messages.json", auth = @Auth(value = "${ApiSid}:${ApiSecret}", type = Type.BASIC))
interface Message {
	@OfMapping(produces = HttpUtils.APPLICATION_FORM_URLENCODED)
	HttpResponse<String> post(@RequestParam("From") String from, @RequestParam("To") String to,
			@RequestParam("Body") String msg);
}
