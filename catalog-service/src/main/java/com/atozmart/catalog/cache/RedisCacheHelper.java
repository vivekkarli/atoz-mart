package com.atozmart.catalog.cache;

import java.time.Duration;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RedisCacheHelper implements CacheHelper {

	private final RedisTemplate<String, ImageCache> redisTemplate;

	private final Long cacheExpiry;

	protected RedisCacheHelper(Long cacheExpiry, RedisConnectionFactory connectionFactory) {
		this.cacheExpiry = cacheExpiry;

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		Jackson2JsonRedisSerializer<ImageCache> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(
				mapper, ImageCache.class);

		this.redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(connectionFactory);
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setHashKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
		redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
		redisTemplate.afterPropertiesSet();
	}

	public void cachePut(String key, ImageCache value) {
		log.info("cache put, key: {}", key);
		try {
			redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(cacheExpiry));
		} catch (Exception e) {
			log.error("Failed to cache object for key {}: {}", key, e.getMessage(), e);
		}
	}

	public boolean cacheEvict(String key) {
		log.info("cache evict, key: {}", key);
		try {
			Boolean deleted = redisTemplate.delete(key);
			return deleted != null && deleted;
		} catch (Exception e) {
			log.error("Failed to evict cache for key {}: {}", key, e.getMessage(), e);
			return false;
		}
	}

	public ImageCache getCache(String key) {
		log.info("cache get, key: {}", key);
		try {
			return redisTemplate.opsForValue().get(key);
		} catch (Exception e) {
			log.error("Failed to retrieve cache for key {}: {}", key, e.getMessage(), e);
		}

		return null;
	}

}
