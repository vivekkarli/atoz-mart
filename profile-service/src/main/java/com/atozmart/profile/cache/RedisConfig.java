package com.atozmart.profile.cache;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

	@Bean
	public RedisCacheHelper redisCacheHelper(RedisConnectionFactory connectionFactory,
			@Value("${atozmart.cache-expiry}") Long cacheExpiry) {
		return new RedisCacheHelper(cacheExpiry, connectionFactory);
	}

	@Bean
	public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory,
			@Value("${atozmart.cache-expiry}") Long cacheExpiry) {

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
