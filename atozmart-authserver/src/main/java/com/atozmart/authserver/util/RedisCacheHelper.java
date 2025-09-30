package com.atozmart.authserver.util;

import java.time.Duration;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.atozmart.authserver.configuration.AtozMartConfig;
import com.atozmart.authserver.configuration.GenericRedisSerializer;

public class RedisCacheHelper<V> {

	private final RedisTemplate<String, V> resdisTemplate;

	private final AtozMartConfig atozMartConfig;

	public RedisCacheHelper(Class<V> valueClass, AtozMartConfig atozMartConfig,
			RedisConnectionFactory connectionFactory) {

		this.resdisTemplate = new RedisTemplate<>();

		resdisTemplate.setConnectionFactory(connectionFactory);
		resdisTemplate.setKeySerializer(new StringRedisSerializer());
		resdisTemplate.setValueSerializer(new GenericRedisSerializer<>(valueClass));
		resdisTemplate.afterPropertiesSet();
		this.atozMartConfig = atozMartConfig;
	}

	public void cachePut(String key, V value) {
		resdisTemplate.expire(key, Duration.ofSeconds(atozMartConfig.cacheExpiry()));
		resdisTemplate.opsForValue().set(key, value, Duration.ofSeconds(atozMartConfig.cacheExpiry()));
	}

	public boolean cacheEvict(String key) {
		return resdisTemplate.delete(key) != null;
	}

	public V getCache(String key) {
		return resdisTemplate.opsForValue().get(key);
	}

}
