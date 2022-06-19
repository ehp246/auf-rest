package me.ehp246.aufrest.core.byrest.perf;


import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import me.ehp246.aufrest.core.byrest.AnnotatedByRest;
import me.ehp246.aufrest.core.byrest.AnnotatedByRest.AuthConfig;
import me.ehp246.aufrest.core.byrest.DefaultProxyMethodParser;
import me.ehp246.aufrest.core.byrest.InvocationRequestBuilder;
import me.ehp246.aufrest.provider.TimingExtension;
import me.ehp246.test.TestUtil;

/**
 * @author Lei Yang
 *
 */
@ExtendWith(TimingExtension.class)
class PerfTest {
    private final int count = 1000_000;
    private final DefaultProxyMethodParser parser = new DefaultProxyMethodParser(p -> "http://localhost", name -> null,
            name -> null, binding -> null);
    private final AnnotatedByRest byRest = new AnnotatedByRest("${uri}", new AuthConfig(), null, "", "", true,
            Object.class, null);

    @Test
    void perf_01() {
        final var captor = TestUtil.newCaptor(PerfTestCase01.class);
        captor.proxy().get(null, null, null, null);
        final var invocation = captor.invocation();
        final InvocationRequestBuilder parsed = parser.parse(invocation.method(), byRest);

        IntStream.range(0, count).forEach(i -> {
            parsed.apply(captor.proxy(), invocation.args().toArray());
        });
    }
}
