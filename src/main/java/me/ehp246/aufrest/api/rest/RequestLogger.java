package me.ehp246.aufrest.api.rest;

import java.net.http.HttpRequest;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Lei Yang
 *
 */
public class RequestLogger implements RequestFilter {
	private final static Logger LOGGER = LogManager.getLogger(RequestLogger.class);
	private final Subscriber<ByteBuffer> subscriber = new Subscriber<>() {

		@Override
		public void onSubscribe(final Subscription subscription) {
			subscription.request(1);
		}

		@Override
		public void onNext(final ByteBuffer item) {
			LOGGER.atTrace().log(new String(item.array(), StandardCharsets.UTF_8));
		}

		@Override
		public void onError(final Throwable throwable) {
			LOGGER.atError().log(throwable);
		}

		@Override
		public void onComplete() {
		}
	};

	@Override
	public HttpRequest apply(final HttpRequest httpRequest, final RequestByRest request) {
		LOGGER.atDebug().log(httpRequest.method() + " " + httpRequest.uri());
		LOGGER.atDebug().log(httpRequest.headers().map());

		httpRequest.bodyPublisher().ifPresentOrElse(pub -> pub.subscribe(subscriber), () -> LOGGER.atDebug().log("-"));

		return httpRequest;
	}

}
