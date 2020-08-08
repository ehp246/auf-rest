package me.ehp246.aufrest.integration.twilio;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Lei Yang
 *
 */
@Disabled
@SpringBootTest(classes = { TwilioApp.class }, properties = { "ApiRoot=https://api.twilio.com/2010-04-01",
		"AccountSid=AC2194f45197f1e5f3fdd57daede06199e", "ApiSid=SK35c96d6723113f9028c7fef2ba6f1b65",
		"ApiSecret=f3NL4s7FEs2nKV3og9eExareKuJpmaLG" })
class EnableByRestTwilioTest {
	@Autowired
	private AutowireCapableBeanFactory factory;

	@Test
	void message_001() {
		factory.getBean(Message.class).post("17542408574", "15132385079", "Hello world");
	}
}
