package org.ehp246.aufrest.core.byrest;

import java.util.Map;

import org.ehp246.aufrest.api.annotation.ByRest;
import org.ehp246.aufrest.api.annotation.OfMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Test cases for path parameters
 *
 * @author Lei Yang
 *
 */
@ByRest("${echo.base}")
interface PathVariableCase001 {
	@OfMapping("/get/{path1}/path2/{path3}")
	void getByPathVariable(@PathVariable("path1") String path1, @PathVariable("path3") String path3);

	@OfMapping("/{path3}/{path4}")
	void getByPathParam(@PathVariable("path4") String path4, @PathVariable("path1") String path1,
			@PathVariable("path3") String path2);

	/**
	 * Map all path ids.
	 *
	 * @param pathParams
	 * @return
	 */
	@OfMapping("/get/{path1}/path2/{path3}")
	void getByMap(@PathVariable Map<String, String> pathParams);

	/**
	 * All path values are merged together. For the same path variable, explicit
	 * parameter value takes precedence over the value, if there is one, from the
	 * un-named map.
	 *
	 * @param pathParams
	 * @param path1
	 * @return
	 */
	@OfMapping("/get/{path1}/path2/{path3}")
	void getByMap(@PathVariable Map<String, String> pathParams, @PathVariable("path1") String path1);

	@OfMapping("/get/{path1}/path2/{path3}")
	void getByObject(@PathVariable PathObject pathObject);

	/**
	 * This method should throw exception since PathParam's are missing.
	 *
	 * @return
	 */
	@OfMapping("/get/{path1}/path2/{path3}")
	void get();

	interface PathObject {
		String getPath1();

		String getPath3();
	}
}
