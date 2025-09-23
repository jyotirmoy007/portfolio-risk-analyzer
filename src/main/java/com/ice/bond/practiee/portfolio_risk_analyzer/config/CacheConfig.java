package com.ice.bond.practiee.portfolio_risk_analyzer.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public Caffeine<Object, Object> caffeineConfig() {
        return Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES) // cache expiry
                .maximumSize(1000); // max entries
    }

    @Bean
    public CacheManager cacheManager(Caffeine<Object, Object> caffeine) {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("bondDurations");
        cacheManager.setCaffeine(caffeine);
        return cacheManager;
    }

    @Bean
    public Cache<String, Double> durationCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(10000)
                .build();
    }
}
