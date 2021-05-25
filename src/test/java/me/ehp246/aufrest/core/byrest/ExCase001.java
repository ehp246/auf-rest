package me.ehp246.aufrest.core.byrest;

import java.io.IOException;

import me.ehp246.aufrest.api.annotation.ByRest;

/**
 * @author Lei Yang
 *
 */
@ByRest("")
interface ExCase001 {
    void get();

    void delete() throws IOException, InterruptedException;

    String post();
}
