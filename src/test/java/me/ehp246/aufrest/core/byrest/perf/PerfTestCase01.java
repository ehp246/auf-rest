package me.ehp246.aufrest.core.byrest.perf;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import me.ehp246.aufrest.api.annotation.OfAuth;
import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfHeader;
import me.ehp246.aufrest.api.annotation.OfMapping;

/**
 * @author Lei Yang
 *
 */
@ByRest("${uri}")
interface PerfTestCase01 {
    @OfMapping("/${uri-context}/clock/{clockName}/{timeZone}")
    void get(@PathVariable("clockName") String clockName, @PathVariable("timeZone") String timeZone,
            @RequestParam("question-1") String query1, @RequestParam("question-2") String query2,
            @OfAuth String auth, @OfHeader Map<String, List<String>> headers, String payload);

    @OfMapping("/${uri-context}/clock/clock-1/EST")
    void get();
}
