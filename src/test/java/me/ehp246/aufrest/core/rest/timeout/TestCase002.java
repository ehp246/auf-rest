package me.ehp246.aufrest.core.rest.timeout;

import me.ehp246.aufrest.api.annotation.ByRest;

@ByRest(value = "", timeout = "-1")
interface TestCase002 {
    void get();
}