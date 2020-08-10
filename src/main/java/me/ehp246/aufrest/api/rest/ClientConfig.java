package me.ehp246.aufrest.api.rest;

import java.time.Duration;

/**
 * @author Lei Yang
 *
 */
public interface ClientConfig {
	default Duration connectTimeout() {
		return null;
	}

	default Duration responseTimeout() {
		return null;
	}

	default TextContentProducer contentProducer(final String mediaType) {
		if (HttpUtils.TEXT_PLAIN.equalsIgnoreCase(mediaType)) {
			return suplier -> suplier.value().toString();
		}
		return null;
	}

	default TextContentConsumer contentConsumer(final String mediaType) {
		if (HttpUtils.TEXT_PLAIN.equalsIgnoreCase(mediaType)) {
			return (text, receiver) -> text;
		}
		return null;
	}
}
