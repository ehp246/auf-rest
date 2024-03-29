package me.ehp246.aufrest.core.rest.perf;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.mock.env.MockEnvironment;

import me.ehp246.aufrest.core.rest.ByRestProxyFactory;
import me.ehp246.aufrest.core.rest.DefaultProxyMethodParser;
import me.ehp246.aufrest.provider.TimingExtension;
import me.ehp246.test.mock.MockBodyHandlerProvider;
import me.ehp246.test.mock.MockRestFn;

/**
 * @author Lei Yang
 *
 */
@ExtendWith(TimingExtension.class)
@EnabledIfEnvironmentVariable(named = "aufrest.perfTest", matches = "true")
class PerfTest {
    private final MockRestFn mockRestFn = new MockRestFn();
    private final int count = 1_000_000;
    private final ByRestProxyFactory factory = new ByRestProxyFactory(mockRestFn.toProvider(),
            new DefaultProxyMethodParser(
                    new MockEnvironment().withProperty("uri", "http://localhost").withProperty("uri-context",
                            "api")::resolveRequiredPlaceholders,
                    name -> null, name -> null, new MockBodyHandlerProvider()));

    private final PerfTestCase01 proxy = factory.newInstance(PerfTestCase01.class);

    @Test
    void perf_01() {
        final var headerMap = Map.of("header-1", List.of("value-1"));

        IntStream.range(0, count).forEach(i -> {
            proxy.get("clock-1", "EST", "asking-1", "asking-2", "auth token", headerMap, null);
        });
    }

    @Test
    void perf_02() {
        IntStream.range(0, count).forEach(i -> {
            proxy.get();
        });
    }
}
