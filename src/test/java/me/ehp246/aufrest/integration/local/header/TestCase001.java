package me.ehp246.aufrest.integration.local.header;

import java.util.List;

import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.Reifying;

/**
 * @author Lei Yang
 *
 */
@ByRest("http://localhost:${local.server.port}/header")
interface TestCase001 {
	@Reifying({ String.class })
	List<String> get(@RequestParam("name") String name, @RequestHeader("x-aufrest-id") String value);

	/*
	 * @Reifying({ String.class, List.class, String.class }) Map<String,
	 * List<String>> get(@RequestHeader("X-aufrest-Trace-Id") String value);
	 *
	 * @Reifying({ String.class, List.class, String.class }) Map<String,
	 * List<String>> get(@RequestHeader Map<String, List<String>> headers);
	 */
}
