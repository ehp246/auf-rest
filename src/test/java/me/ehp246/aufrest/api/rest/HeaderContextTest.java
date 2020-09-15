package me.ehp246.aufrest.api.rest;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import me.ehp246.aufrest.core.util.InvocationUtil;

/**
 * @author Lei Yang
 *
 */
class HeaderContextTest {
	private static class Runner implements Callable<Map<String, List<String>>> {
		private final int counter;

		Runner(final int counter) {
			super();
			this.counter = counter;
		}

		@Override
		public Map<String, List<String>> call() {
			var headers = HeaderContext.headers();

			Assertions.assertEquals(1, headers.size());
			Assertions.assertEquals("main", headers.get("x-trace-id").get(0));

			sleep();

			HeaderContext.header("x-counter", counter + "");

			sleep();

			headers = HeaderContext.headers();

			HeaderContext.remove("x-counter");

			return headers;
		}

		private void sleep() {
			try {
				Thread.sleep((long) (Math.random() * 100));
			} catch (final InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

	}

	@Test
	void test() throws InterruptedException, ExecutionException {
		Assertions.assertEquals(0, HeaderContext.headers().size());

		// Should propagate to child threads
		HeaderContext.header("x-trace-id", "main");

		final var count = 20;

		final var pool = Executors.newFixedThreadPool(count / 2);

		final var futures = IntStream.range(0, count).boxed()
				.map(i -> CompletableFuture.supplyAsync(new Runner(i)::call, pool)).collect(Collectors.toList());

		CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).get();

		final var headers = futures.stream().map(future -> InvocationUtil.invoke(future::get))
				.collect(Collectors.toList());

		Assertions.assertEquals(count, headers.size());
		IntStream.range(0, count).forEach(i -> {
			Assertions.assertEquals("main", headers.get(i).get("x-trace-id").get(0));
			Assertions.assertEquals(i + "", headers.get(i).get("x-counter").get(0));
		});

		Assertions.assertEquals(1, HeaderContext.headers().size());
	}

}
