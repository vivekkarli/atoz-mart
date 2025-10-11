package com.atozmart.catalog.cache;

public interface CacheHelper {
	
	void cachePut(String key, ImageCache value);

	boolean cacheEvict(String key);

	ImageCache getCache(String key);

}
