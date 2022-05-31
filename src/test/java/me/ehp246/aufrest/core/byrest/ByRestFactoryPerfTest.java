package me.ehp246.aufrest.core.byrest;

import java.net.http.HttpResponse;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import me.ehp246.aufrest.provider.TimingExtension;

/**
 * @author Lei Yang
 *
 */
@Disabled
@ExtendWith(TimingExtension.class)
class ByRestFactoryPerfTest {
    private final ByRestProxyFactory factory = new ByRestProxyFactory(cfg -> req -> Mockito.mock(HttpResponse.class),
            Object::toString);
    private final PerfTestCases.Case001 case01 = factory.newInstance(PerfTestCases.Case001.class);
    private final int count = 100000;

    @Test
    void test_01() {
        IntStream.range(0, count).forEach(i -> case01.get());
    }

    @Test
    void test_02() {
        for (int i = 0; i <= count; i++) {
            case01.get();
        }
    }
}
