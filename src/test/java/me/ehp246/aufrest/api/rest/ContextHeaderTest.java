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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import me.ehp246.aufrest.core.util.InvocationUtil;

/**
 * @author Lei Yang
 *
 */
class ContextHeaderTest {
	private static class Runner implements Callable<Map<String, List<String>>> {
		private final int id;

		Runner(final int id) {
			super();
			this.id = id;
		}

		@Override
		public Map<String, List<String>> call() {
			var headers = ContextHeader.copyAsMap();

			Assertions.assertEquals(1, headers.size());
			Assertions.assertEquals("main", headers.get("x-trace-id").get(0));

			sleep();

			ContextHeader.add("x-trace-id", id + "");
			ContextHeader.add("x-id", id + "");

			sleep();

			headers = ContextHeader.copyAsMap();

			ContextHeader.remove("x-id");

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

	@BeforeEach
	void clear() {
		ContextHeader.removeAll();
	}

	@Test
	void thread_001() throws InterruptedException, ExecutionException {
		Assertions.assertEquals(0, ContextHeader.copyAsMap().size());

		// Should propagate to child threads
		ContextHeader.add("x-trace-id", "main");

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

		Assertions.assertEquals(1, ContextHeader.copyAsMap().size());
		Assertions.assertEquals(1, ContextHeader.copyAsMap().get("x-trace-id").size());
	}

	@Test
	void copy_001() {
		ContextHeader.add("trace-id", "1");

		ContextHeader.copyAsMap().clear();

		Assertions.assertEquals(1, ContextHeader.get("trace-id").size(),
				"should be separate snapshots without new updates");
	}

	@Test
	void add_001() {
		Assertions.assertEquals(0, ContextHeader.get("").size());

		ContextHeader.add("x-trace-id", "1");

		ContextHeader.get("x-trace-id").add("2");

		Assertions.assertEquals(2, ContextHeader.get("x-trace-id").size());
	}

	@Test
	void set_001() {
		ContextHeader.add("x-trace-id", "1");

		ContextHeader.set("x-trace-id", "2");

		Assertions.assertEquals("[2]", ContextHeader.get("x-trace-id").toString());
	}

	@Test
	void remove_001() {
		ContextHeader.add("x-trace-id", "1");

		ContextHeader.remove("x-trace-id");

		Assertions.assertEquals(0, ContextHeader.get("x-trace-id").size());

		ContextHeader.add("x-trace-id", "1");
		ContextHeader.add("x-trace-id-1", "1");

		ContextHeader.removeAll();

		Assertions.assertEquals(0, ContextHeader.copyAsMap().size());
	}

	@Test
	void casing_001() {
		ContextHeader.add("x-trace-id", "1");
		ContextHeader.add("x-trace-Id", "2");

		Assertions.assertEquals(2, ContextHeader.copyAsMap().get("x-trace-iD".toLowerCase()).size());

		ContextHeader.set("x-Trace-Id", "1");

		Assertions.assertEquals(1, ContextHeader.copyAsMap().get("x-trace-iD".toLowerCase()).size());

		ContextHeader.remove("X-Trace-Id");

		Assertions.assertEquals(null, ContextHeader.copyAsMap().get("x-trace-iD".toLowerCase()));
	}
}
