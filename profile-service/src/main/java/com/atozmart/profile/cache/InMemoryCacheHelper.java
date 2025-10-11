package com.atozmart.profile.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import com.atozmart.profile.dto.ProfilePhotoCache;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InMemoryCacheHelper implements CacheHelper {

	private final CacheManager cacheManager;

	protected InMemoryCacheHelper(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
		log.info("InMemoryCacheHelper created");
	}

	@Override
	public void cachePut(String key, ProfilePhotoCache value) {
		log.info("cache put, key: {}", key);
		try {
			Cache cache = cacheManager.getCache(key);
			cache.put(key, value);
		} catch (Exception e) {
			log.error("Failed to cache object for key {}: {}", key, e.getMessage(), e);
		}
	}

	@Override
	public boolean cacheEvict(String key) {
		log.info("cache evict, key: {}", key);
		try {
			Cache cache = cacheManager.getCache(key);
			Boolean deleted = cache.evictIfPresent(key);
			return deleted != null && deleted;
		} catch (Exception e) {
			log.error("Failed to evict cache for key {}: {}", key, e.getMessage(), e);
			return false;
		}
	}

	@Override
	public ProfilePhotoCache getCache(String key) {
		log.info("cache get, key: {}", key);
		try {
			Cache cache = cacheManager.getCache(key);
			return cache.get(key, ProfilePhotoCache.class);
		} catch (Exception e) {
			log.error("Failed to retrieve cache for key {}: {}", key, e.getMessage(), e);
		}

		return null;
	}

}
