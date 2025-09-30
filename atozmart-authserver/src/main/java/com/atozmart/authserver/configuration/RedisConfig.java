package com.atozmart.authserver.configuration;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.atozmart.authserver.dto.AppUserDto;
import com.atozmart.authserver.util.RedisCacheHelper;

@Configuration
public class RedisConfig {

	@Bean
	public RedisCacheHelper<AppUserDto> appUserCacheHelper(RedisConnectionFactory connectionFactory,
			AtozMartConfig atozMartConfig) {

		return new RedisCacheHelper<>(AppUserDto.class, atozMartConfig, connectionFactory);
	}

	@Bean
	public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {

		RedisCacheConfiguration defaultRedisConfig = RedisCacheConfiguration.defaultCacheConfig()
				.entryTtl(Duration.ofMinutes(1))
				.serializeKeysWith(
						RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
				.serializeValuesWith(
						RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
				.disableCachingNullValues();

		return RedisCacheManager.builder(connectionFactory).cacheDefaults(defaultRedisConfig).build();
	}

}
