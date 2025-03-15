package com.atozmart.authserver.dao;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import com.atozmart.authserver.entity.AppUser;
import com.atozmart.authserver.repository.AppUserRepository;

import lombok.AllArgsConstructor;

@Repository
@AllArgsConstructor
public class AppUserDao implements UserDetailsService{
	
	private AppUserRepository appUserRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<AppUser> appUserOpt = appUserRepository.findById(username);
		return appUserOpt.get();
	}
	
	public void signUp(AppUser appUser) {
		
		//appUserRepository.existsById(appUser.getUsername())
		
		appUserRepository.save(appUser);
	}
	

}
