package com.atozmart.authserver.cache;

import com.atozmart.authserver.dto.AppUserDto;

public interface CacheHelper {
	
	void cachePut(String key, AppUserDto value);

	boolean cacheEvict(String key);

	AppUserDto getCache(String key);

}
