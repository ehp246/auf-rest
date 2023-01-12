package me.ehp246.test.embedded.cache;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfRequest;

/**
 * @author Lei Yang
 *
 */
@ByRest(value = "http://localhost:${local.server.port}/cache")
@CacheConfig(cacheNames = "inc")
interface CacheCase {
    @OfRequest("/inc")
    @Cacheable
    int postInc();
}
