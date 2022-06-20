package me.ehp246.aufrest.core.byrest.perf;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.mock.env.MockEnvironment;

import me.ehp246.aufrest.api.rest.ClientConfig;
import me.ehp246.aufrest.core.byrest.ByRestProxyFactory;
import me.ehp246.aufrest.core.byrest.DefaultProxyMethodParser;
import me.ehp246.aufrest.mock.MockHttpResponse;
import me.ehp246.aufrest.mock.MockRestFnProvider;
import me.ehp246.aufrest.provider.TimingExtension;

/**
 * @author Lei Yang
 *
 */
@ExtendWith(TimingExtension.class)
@EnabledIfEnvironmentVariable(named = "aufrest.perfTest", matches = "true")
class PerfTest {
    private final int count = 1_000_000;

    private final ByRestProxyFactory factory = new ByRestProxyFactory(new MockRestFnProvider(new MockHttpResponse<>()),
            new ClientConfig(),
            new DefaultProxyMethodParser(new MockEnvironment().withProperty("uri", "http://localhost")
                    .withProperty("uri-context", "api")::resolveRequiredPlaceholders, name -> null, name -> null,
                    binding -> null));

    @Test
    void perf_01() {
        final var proxy = factory.newInstance(PerfTestCase01.class);
        final var headerMap = Map.of("header-1", List.of("value-1"));

        IntStream.range(0, count).forEach(i -> {
            proxy.get("clock-1", "EST", "asking-1", "asking-2", "auth token", headerMap, null);
        });
    }
}
