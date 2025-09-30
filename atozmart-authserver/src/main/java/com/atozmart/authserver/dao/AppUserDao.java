package com.atozmart.authserver.dao;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import com.atozmart.authserver.dto.AppUserDto;
import com.atozmart.authserver.dto.profile.BasicDetailsDto;
import com.atozmart.authserver.entity.AppUser;
import com.atozmart.authserver.exception.AuthServerException;
import com.atozmart.authserver.repository.AppUserRepository;
import com.atozmart.authserver.util.RedisCacheHelper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AppUserDao implements UserDetailsService {

	private final AppUserRepository appUserRepository;

	private final RedisCacheHelper<AppUserDto> appUserCacheHelper;

	private static final String CACHE_PREFIX = "app-user::";

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		AppUserDto cachedAppUser = appUserCacheHelper.getCache(CACHE_PREFIX + username);

		if (cachedAppUser != null)
			return cachedAppUser;

		AppUser appUser = appUserRepository.findById(username)
				.orElseThrow(() -> new UsernameNotFoundException("user not found"));

		AppUserDto appUserDto = new AppUserDto(appUser);
		appUserCacheHelper.cachePut(CACHE_PREFIX + username, appUserDto);
		return appUserDto;
	}

	public void signUp(AppUser appUser) throws AuthServerException {
		if (appUserRepository.existsById(appUser.getUsername()))
			throw new AuthServerException("user already exists", HttpStatus.BAD_REQUEST);
		AppUser appUserUpdated = appUserRepository.save(appUser);

		// cache put
		AppUserDto appUserDtoUpdated = new AppUserDto(appUserUpdated);
		appUserCacheHelper.cachePut(CACHE_PREFIX + appUserDtoUpdated.getUsername(), appUserDtoUpdated);
	}

	public void updateUser(AppUser appUser) throws AuthServerException {
		if (!appUserRepository.existsById(appUser.getUsername()))
			throw new AuthServerException("user doesn't exists", HttpStatus.BAD_REQUEST);
		AppUser appUserUpdated = appUserRepository.save(appUser);

		// cache put
		AppUserDto appUserDtoUpdated = new AppUserDto(appUserUpdated);
		appUserCacheHelper.cachePut(CACHE_PREFIX + appUserDtoUpdated.getUsername(), appUserDtoUpdated);
	}

	public void updateBasicDetails(String username, BasicDetailsDto basicDetailsDto) {

		AppUser appUser = appUserRepository.findById(username)
				.orElseThrow(() -> new UsernameNotFoundException("user not found"));
		appUser.setMail(basicDetailsDto.mail());
		appUser.setMobileNo(basicDetailsDto.mobileNo());

		// cache put
		AppUserDto appUserDtoUpdated = new AppUserDto(appUser);
		appUserCacheHelper.cachePut(CACHE_PREFIX + appUserDtoUpdated.getUsername(), appUserDtoUpdated);

	}

}
