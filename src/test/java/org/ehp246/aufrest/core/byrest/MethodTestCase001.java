package org.ehp246.aufrest.core.byrest;

import org.ehp246.aufrest.api.annotation.ByRest;
import org.ehp246.aufrest.api.annotation.OfMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Lei Yang
 *
 */
@ByRest("")
interface MethodTestCase001 {
	void get();

	void getBySomething();

	void query(int i);

	@OfMapping(method = RequestMethod.GET)
	void query();

	void post();

	void postByName();

	@OfMapping(method = RequestMethod.POST)
	void create();

	void delete();

	@OfMapping(method = RequestMethod.DELETE)
	void remove();

	void put();

	void patch();
}
