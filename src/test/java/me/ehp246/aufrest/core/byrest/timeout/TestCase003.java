package me.ehp246.aufrest.core.byrest.timeout;

import me.ehp246.aufrest.api.annotation.ByRest;

@ByRest(value = "", timeout = "PT11.021S")
interface TestCase003 {
	void get();
}