package me.ehp246.aufrest.core.byrest;

import java.util.Map;

import org.springframework.web.bind.annotation.RequestParam;

import me.ehp246.aufrest.api.annotation.ByRest;

/**
 * @author Lei Yang
 *
 */
@ByRest("${echo.base}/get")
interface RequestParamCase001 {
	void queryByParams(@RequestParam("query1") String query1, @RequestParam("query2") String query2);

	void queryEncoded(@RequestParam("query 1") String query1);

	void getByMap(@RequestParam Map<String, String> queryParams);

	void getByMap(@RequestParam Map<String, String> queryParams, @RequestParam("query2") String query2);

	void getByObject(@RequestParam QueryObject queryObject);

	interface QueryObject {
		String getFirstName();

		String getLastName();
	}

}
