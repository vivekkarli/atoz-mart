package com.atozmart.authserver.util;

import java.time.Duration;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import com.atozmart.authserver.configuration.GenericRedisSerializer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RedisCacheHelper<K, V> {

	private final RedisTemplate<K, V> resdisTemplate;

	private final Long cacheExpiry;

	public RedisCacheHelper(Class<K> keyClass, Class<V> valueClass, Long cacheExpiry,
			RedisConnectionFactory connectionFactory) {

		this.cacheExpiry = cacheExpiry;
		this.resdisTemplate = new RedisTemplate<>();

		resdisTemplate.setConnectionFactory(connectionFactory);
		resdisTemplate.setKeySerializer(new GenericRedisSerializer<>(keyClass));
		resdisTemplate.setValueSerializer(new GenericRedisSerializer<>(valueClass));
		resdisTemplate.afterPropertiesSet();
	}

	public void cachePut(K key, V value) {
		log.info("cache put, key: {}", key);
		resdisTemplate.opsForValue().set(key, value, Duration.ofSeconds(cacheExpiry));
	}

	public boolean cacheEvict(K key) {
		log.info("cache evict, key: {}", key);
		return resdisTemplate.delete(key) != null;
	}

	public V getCache(K key) {
		log.info("cache get, key: {}", key);
		return resdisTemplate.opsForValue().get(key);
	}

}
