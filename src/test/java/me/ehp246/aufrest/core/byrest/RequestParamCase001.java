package me.ehp246.aufrest.core.byrest;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestParam;

import me.ehp246.aufrest.api.annotation.OfMapping;

/**
 * @author Lei Yang
 *
 */
interface RequestParamCase001 {
    void getByParams(@RequestParam("query1") String query1, @RequestParam("query2") String query2);

    @OfMapping(method = "GET")
    void queryEncoded(@RequestParam("query 1") String query1);

    void getByMap(@RequestParam Map<String, String> queryParams);

    void getByMap(@RequestParam Map<String, String> queryParams, @RequestParam("query2") String query2);

    void getRepeated(@RequestParam("query 1") String query1, @RequestParam("query 1") String query2);

    void getByList(@RequestParam("qList") List<String> q);
}
