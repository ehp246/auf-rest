package me.ehp246.aufrest.core.byrest;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfMapping;

/**
 * @author Lei Yang
 *
 */
@ByRest("${echo.base}/")
interface GetCase001 {
	/**
	 * Should be getting from the type.
	 *
	 * @param str
	 * @return
	 */
	@OfMapping("get")
	void get();

	/**
	 * Should be getting from the method.
	 *
	 * @param param1
	 * @param value1
	 * @param param2
	 * @param value2
	 * @return
	 */
	@OfMapping("get1")
	void get(String str);

	@OfMapping
	void get(Integer i);
}
