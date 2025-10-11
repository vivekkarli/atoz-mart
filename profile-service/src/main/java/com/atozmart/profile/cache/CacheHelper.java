package com.atozmart.profile.cache;

import com.atozmart.profile.dto.ProfilePhotoCache;

public interface CacheHelper {
	
	void cachePut(String key, ProfilePhotoCache value);

	boolean cacheEvict(String key);

	ProfilePhotoCache getCache(String key);

}
