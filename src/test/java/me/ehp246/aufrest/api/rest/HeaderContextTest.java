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
		private final int id;

		Runner(final int id) {
			super();
			this.id = id;
		}

		@Override
		public Map<String, List<String>> call() {
			var headers = HeaderContext.headers();

			Assertions.assertEquals(1, headers.size());
			Assertions.assertEquals("main", headers.get("x-trace-id").get(0));

			sleep();

			HeaderContext.header("x-id", id + "");

			sleep();

			headers = HeaderContext.headers();

			HeaderContext.remove("x-id");

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
			Assertions.assertEquals(i + "", headers.get(i).get("x-id").get(0));
		});

		Assertions.assertEquals(1, HeaderContext.headers().size());
	}

	@Test
	void modification_001() {
		final var headers1 = HeaderContext.headers();

		Assertions.assertThrows(Exception.class, headers1::clear, "should not be able to modify map");

		HeaderContext.header("x-trace-id", "1");

		final var headers2 = HeaderContext.headers();

		final var list = headers2.get("x-trace-id");

		Assertions.assertEquals("1", list.get(0));

		Assertions.assertThrows(Exception.class, () -> list.add(""), "should not be able to modify value list");

		Assertions.assertEquals(0, headers1.size(), "should be separate snapshots without new updates");
	}

}
