package com.atozmart.authserver.dao;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import com.atozmart.authserver.cache.CacheHelper;
import com.atozmart.authserver.dto.AppUserDto;
import com.atozmart.authserver.dto.profile.BasicDetailsDto;
import com.atozmart.authserver.entity.AppUser;
import com.atozmart.authserver.exception.AuthServerException;
import com.atozmart.authserver.repository.AppUserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AppUserDao implements UserDetailsService {

	private final AppUserRepository appUserRepository;

	private final CacheHelper appUserCacheHelper;

	private static final String CACHE_PREFIX;

	static {
		CACHE_PREFIX = "app-user::";
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return getUser(username);
	}

	public AppUserDto getUser(String username) {
		AppUserDto cachedAppUser = appUserCacheHelper.getCache(CACHE_PREFIX + username);
		if (cachedAppUser != null) {
			log.info("cache hit, key: {}", CACHE_PREFIX + username);
			return cachedAppUser;
		}

		log.info("cache miss, key: {}", CACHE_PREFIX + username);
		AppUser appUser = appUserRepository.findById(username)
				.orElseThrow(() -> new UsernameNotFoundException("user not found"));
		AppUserDto appUserDto = new AppUserDto(appUser);

		/*
		 * Cache the user
		 */
		appUserCacheHelper.cachePut(CACHE_PREFIX + username, appUserDto);
		return appUserDto;
	}

	public void createUser(AppUserDto appUserDto) throws AuthServerException {
		if (appUserRepository.existsById(appUserDto.getUsername()))
			throw new AuthServerException("user already exists", HttpStatus.BAD_REQUEST);

		AppUser appUser = new AppUser(appUserDto);
		appUserRepository.save(appUser);
	}

	public void updateUser(AppUserDto appUserDto) throws AuthServerException {
		if (!appUserRepository.existsById(appUserDto.getUsername()))
			throw new AuthServerException("user doesn't exists", HttpStatus.BAD_REQUEST);
		AppUser appUser = new AppUser(appUserDto);
		appUserRepository.save(appUser);
	}

	public void updateBasicDetails(String username, BasicDetailsDto basicDetailsDto) {
		AppUser appUser = appUserRepository.findById(username)
				.orElseThrow(() -> new UsernameNotFoundException("user not found"));
		appUser.setMail(basicDetailsDto.mail());
		appUser.setMobileNo(basicDetailsDto.mobileNo());
	}

}
