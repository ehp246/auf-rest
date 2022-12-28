package me.ehp246.test.local.cache;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfMapping;

/**
 * @author Lei Yang
 *
 */
@ByRest(value = "http://localhost:${local.server.port}/cache")
@CacheConfig(cacheNames = "inc")
interface CacheCase {
    @OfMapping("/inc")
    @Cacheable
    int postInc();
}
