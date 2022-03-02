package me.ehp246.aufrest.core.reflection;

import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Lei Yang
 *
 */
interface TestCase {
    void scanArgs(@RequestParam("query 1") String q1);

    void scanArgs02(@RequestParam("query 1") String q1, @RequestParam("query 2") String q2);

    void scanArgs03(@RequestParam("query 1") String q1, @RequestParam("query 1") String q2);
}
