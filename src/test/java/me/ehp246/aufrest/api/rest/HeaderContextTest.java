package me.ehp246.aufrest.api.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Lei Yang
 *
 */
class HeaderContextTest {
	@BeforeEach
	void beforeEach() {
		HeaderContext.remove();
	}

	@BeforeAll
	@AfterAll
	static void clear() {
		// To avoid polluting the thread in Maven test.
		HeaderContext.remove();
	}

	@Test
	void map_001() {
		final var headers1 = HeaderContext.map();

		Assertions.assertThrows(Exception.class, headers1::clear, "should not be able to modify map");

		HeaderContext.add("x-trace-id", "1");

		final var headers2 = HeaderContext.map();

		final var list = headers2.get("x-trace-id");

		Assertions.assertEquals("1", list.get(0));

		Assertions.assertThrows(Exception.class, () -> list.add(UUID.randomUUID().toString()),
				"should not be able to modify value list");

		Assertions.assertEquals(0, headers1.size(), "should be separate snapshots without new updates");
	}

	@Test
	void values_001() {
		Assertions.assertThrows(Exception.class, () -> HeaderContext.values(null));

		Assertions.assertThrows(Exception.class, () -> HeaderContext.values(""));
		Assertions.assertThrows(Exception.class, () -> HeaderContext.values("  "));

		Assertions.assertEquals(0, HeaderContext.values(UUID.randomUUID().toString()).size());
	}

	@Test
	void add_001() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> HeaderContext.add(null, "1"));
		Assertions.assertThrows(IllegalArgumentException.class, () -> HeaderContext.add("", "1"));
		Assertions.assertThrows(IllegalArgumentException.class, () -> HeaderContext.add(" ", "1"));

		Assertions.assertThrows(NullPointerException.class, () -> HeaderContext.add("x-trace-id", (String) null));

		HeaderContext.add("x-trace-id", "1");

		Assertions.assertEquals(1, HeaderContext.values("x-trace-id").size());

		HeaderContext.add("X-trace-id", "1");

		Assertions.assertEquals(2, HeaderContext.values("x-Trace-id").size(), "should be case-insensitive");
	}

	@Test
	void add_list_002() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> HeaderContext.add(null, List.of("1")));
		Assertions.assertThrows(IllegalArgumentException.class, () -> HeaderContext.add("", List.of("1")));
		Assertions.assertThrows(IllegalArgumentException.class, () -> HeaderContext.add("   ", List.of("1")));

		Assertions.assertThrows(NullPointerException.class, () -> HeaderContext.add("x-trace-id", (List<String>) null));

		final var list = new ArrayList<String>();
		list.add(UUID.randomUUID().toString());
		list.add(null);
		list.add("");
		list.add("   ");
		list.add(UUID.randomUUID().toString());

		HeaderContext.add("x-trace-id", Collections.unmodifiableList(list));

		Assertions.assertEquals(2, HeaderContext.values("x-trace-id").size(), "should filter null, empty, and blank");
	}

	@Test
	void add_003() {
		Assertions.assertThrows(NullPointerException.class,
				() -> HeaderContext.merge((Map<String, List<String>>) null));

		final var list = new ArrayList<String>();
		list.add("123");
		list.add(null);
		list.add("");
		list.add("   ");
		list.add("123");

		HeaderContext.merge(Map.of("", Collections.unmodifiableList(list)));

		Assertions.assertEquals(0, HeaderContext.map().size());

		HeaderContext.merge(Map.of("x-trace-id", Collections.unmodifiableList(list)));

		Assertions.assertEquals(1, HeaderContext.map().size());

		Assertions.assertEquals(2, HeaderContext.values("x-trace-id").size());
	}

	@Test
	void set_001() {
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> HeaderContext.set(null, UUID.randomUUID().toString()));
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> HeaderContext.set("", UUID.randomUUID().toString()));
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> HeaderContext.set("  ", UUID.randomUUID().toString()));

		Assertions.assertThrows(Exception.class, () -> HeaderContext.set(UUID.randomUUID().toString(), (String) null));
	}

	@Test
	void set_002() {
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> HeaderContext.set(null, List.of(UUID.randomUUID().toString())));
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> HeaderContext.set("", List.of(UUID.randomUUID().toString())));
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> HeaderContext.set(" \r\n ", List.of(UUID.randomUUID().toString())));

		HeaderContext.set("x-trace-id", List.of(UUID.randomUUID().toString(), UUID.randomUUID().toString()));

		Assertions.assertEquals(2, HeaderContext.values("X-trace-id").size());

		HeaderContext.add("x-Trace-id", List.of(UUID.randomUUID().toString(), UUID.randomUUID().toString()));

		Assertions.assertEquals(4, HeaderContext.values("X-trace-ID").size());
	}

	@Test
	void set_003() {
		HeaderContext.add("x-request-id", List.of(UUID.randomUUID().toString(), UUID.randomUUID().toString()));
		HeaderContext.add("x-Trace-id", List.of(UUID.randomUUID().toString(), UUID.randomUUID().toString()));

		HeaderContext.set(Map.of("X-trace-id", List.of(UUID.randomUUID().toString(), UUID.randomUUID().toString())));

		Assertions.assertEquals(1, HeaderContext.map().size());
		Assertions.assertEquals(2, HeaderContext.values("X-trace-ID").size());
	}

	@Test
	void remove_001() {
		// Should we care?
		Assertions.assertDoesNotThrow(() -> HeaderContext.remove(null));
		Assertions.assertDoesNotThrow(() -> HeaderContext.remove(""));
		Assertions.assertDoesNotThrow(() -> HeaderContext.remove("  "));

		HeaderContext.add("x-trace-id", "1");

		HeaderContext.remove("x-trace-id");

		Assertions.assertEquals(0, HeaderContext.values("x-trace-id").size());

		HeaderContext.add("x-trace-id", "1");
		HeaderContext.add("x-trace-id-1", "1");

		HeaderContext.remove();

		Assertions.assertEquals(0, HeaderContext.map().size());
	}

	@Test
	void casing_001() {
		HeaderContext.add("x-trace-id", "1");
		HeaderContext.add("x-trace-Id", "2");

		Assertions.assertEquals(2, HeaderContext.map().get("x-trace-iD").size());

		HeaderContext.set("x-Trace-Id", "1");

		Assertions.assertEquals(1, HeaderContext.map().get("x-trace-iD").size());

		HeaderContext.remove("X-Trace-Id");

		Assertions.assertEquals(null, HeaderContext.map().get("x-trace-iD"));
	}
}
