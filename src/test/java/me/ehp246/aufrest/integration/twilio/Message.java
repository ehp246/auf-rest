package me.ehp246.aufrest.integration.twilio;

import org.springframework.web.bind.annotation.RequestParam;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.ByRest.Auth;
import me.ehp246.aufrest.api.annotation.ByRest.Auth.Type;

/**
 * @author Lei Yang
 *
 */
@ByRest(value = "https://api.twilio.com/2010-04-01/Accounts/${AccountSid}/Messages.json", auth = @Auth(value = "${ApiSid}:${ApiSecret}", type = Type.BASIC))
interface Message {
	void post(@RequestParam("From") String from, @RequestParam("To") String to, @RequestParam("Body") String msg);
}
