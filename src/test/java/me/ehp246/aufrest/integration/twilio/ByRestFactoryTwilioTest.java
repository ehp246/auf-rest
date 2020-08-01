package me.ehp246.aufrest.integration.twilio;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.mock.env.MockEnvironment;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.mrbean.MrBeanModule;

import me.ehp246.aufrest.api.rest.ClientConfig;
import me.ehp246.aufrest.core.byrest.ByRestFactory;
import me.ehp246.aufrest.provider.httpclient.JdkClientProvider;
import me.ehp246.aufrest.provider.jackson.JsonByJackson;

/**
 * @author Lei Yang
 *
 */
class ByRestFactoryTwilioTest {
	private final JdkClientProvider client = new JdkClientProvider(new ClientConfig() {
	});
	private final MockEnvironment env = new MockEnvironment()
			.withProperty("AccountSid", "AC2194f45197f1e5f3fdd57daede06199e")
			.withProperty("ApiSid", "SK35c96d6723113f9028c7fef2ba6f1b65")
			.withProperty("ApiSecret", "f3NL4s7FEs2nKV3og9eExareKuJpmaLG");
	private final ObjectMapper objectMapper = new ObjectMapper().setSerializationInclusion(Include.NON_NULL)
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).registerModule(new JavaTimeModule())
			.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).registerModule(new MrBeanModule());
	private final JsonByJackson bodyBuilder = new JsonByJackson(objectMapper);
	private final ByRestFactory factory = new ByRestFactory(client, env, bodyBuilder.getFromText(),
			bodyBuilder.getToText(), new DefaultListableBeanFactory());

	void message_001() {
		factory.newInstance(Message.class).post("17542408574", "15132385079", "Hello world");
	}
}
