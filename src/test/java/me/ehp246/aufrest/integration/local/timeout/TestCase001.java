package me.ehp246.aufrest.integration.local.timeout;

import org.springframework.web.bind.annotation.RequestParam;

import me.ehp246.aufrest.api.annotation.ByRest;

/**
 * @author Lei Yang
 *
 */
@ByRest(value = "http://localhost:${local.server.port}/timeout", timeout = "${api.request.timeout:}")
interface TestCase001 {
	void get();

	void get(@RequestParam("sleep") String sleep);
}
