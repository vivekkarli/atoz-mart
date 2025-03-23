package com.atozmart.authserver.dao;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import com.atozmart.authserver.entity.AppUser;
import com.atozmart.authserver.exception.AuthServerException;
import com.atozmart.authserver.repository.AppUserRepository;

import lombok.AllArgsConstructor;

@Repository
@AllArgsConstructor
public class AppUserDao implements UserDetailsService {

	private AppUserRepository appUserRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return appUserRepository.findById(username).get();
	}

	public void signUp(AppUser appUser) {
		if(appUserRepository.existsById(appUser.getUsername()))
			throw new AuthServerException("user already exists");
		appUserRepository.save(appUser);
	}

}
