package com.atozmart.gatewayserver.dao;

import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Repository;

import com.atozmart.gatewayserver.entity.AppUser;
import com.atozmart.gatewayserver.repository.AppUserRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
@AllArgsConstructor
public class GatewayDao implements UserDetailsManager, ReactiveUserDetailsService {

	private final AppUserRepository appUserRepo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return appUserRepo.findById(username).orElseThrow(() -> new UsernameNotFoundException(username));
	}

	@Override
	public Mono<UserDetails> findByUsername(String username) {
		AppUser appuser = appUserRepo.findById(username).orElseThrow(() -> new UsernameNotFoundException(username));

		return Mono.justOrEmpty(appuser);
	}

	public void createOrUpdateAppUser(AppUser appUser) {
		appUserRepo.save(appUser);
	}

	@Override
	public void changePassword(String oldPassword, String newPassword) {

	}

	@Override
	public boolean userExists(String username) {
		return appUserRepo.existsById(username);
	}

	@Override
	public void createUser(UserDetails user) {

	}

	@Override
	public void updateUser(UserDetails user) {

	}

	@Override
	public void deleteUser(String username) throws UsernameNotFoundException{
		if (userExists(username)) {
			appUserRepo.deleteById(username);
		} else {
			throw new UsernameNotFoundException(username + " not found");
		}

	}

}
