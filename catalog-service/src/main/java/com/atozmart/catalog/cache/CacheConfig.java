package com.atozmart.catalog.cache;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class CacheConfig {

	@Bean
	@Profile("default")
	public CacheHelper inMemoryCacheHelper() {
		ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
		return new InMemoryCacheHelper(cacheManager);
	}

	@Bean
	@Profile("prod")
	public CacheHelper redisCacheHelper(@Value("${catalog-service.cache-expiry:360}") Long cacheExpiry,
			RedisConnectionFactory connectionFactory) {
		return new RedisCacheHelper(cacheExpiry, connectionFactory);
	}

	@Bean
	@Profile("prod")
	public CacheManager redisCacheManager(RedisConnectionFactory connectionFactory,
			@Value("${catalog-service.cache-expiry:360}") Long cacheExpiry) {

		RedisCacheConfiguration defaultRedisConfig = RedisCacheConfiguration.defaultCacheConfig()
				.entryTtl(Duration.ofSeconds(cacheExpiry))
				.serializeKeysWith(
						RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
				.serializeValuesWith(
						RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
				.disableCachingNullValues();

		return RedisCacheManager.builder(connectionFactory).cacheDefaults(defaultRedisConfig).build();
	}

}
