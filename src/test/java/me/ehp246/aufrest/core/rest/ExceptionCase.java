package me.ehp246.aufrest.core.rest;

import java.io.IOException;

import me.ehp246.aufrest.api.annotation.ByRest;

/**
 * @author Lei Yang
 *
 */
@ByRest("")
interface ExceptionCase {
    void get();

    void delete() throws IOException, InterruptedException;

    String post();
}
