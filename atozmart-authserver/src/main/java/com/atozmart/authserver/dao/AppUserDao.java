package com.atozmart.authserver.dao;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import com.atozmart.authserver.entity.AppUser;
import com.atozmart.authserver.exception.AuthServerException;
import com.atozmart.authserver.repository.AppUserRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AppUserDao implements UserDetailsService {

	private final AppUserRepository appUserRepository;

	@Override
	public AppUser loadUserByUsername(String username) throws UsernameNotFoundException {
		return appUserRepository.findById(username).orElseThrow(() -> new UsernameNotFoundException("user not found"));
	}

	public void signUp(AppUser appUser) throws AuthServerException {
		if (appUserRepository.existsById(appUser.getUsername()))
			throw new AuthServerException("user already exists", HttpStatus.BAD_REQUEST);
		appUserRepository.save(appUser);
	}

}
