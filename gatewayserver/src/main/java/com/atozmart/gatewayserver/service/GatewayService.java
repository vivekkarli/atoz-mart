package com.atozmart.gatewayserver.service;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.atozmart.gatewayserver.dao.GatewayDao;
import com.atozmart.gatewayserver.entity.AppUser;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class GatewayService {

	private final PasswordEncoder passwordEncoder;

	private final GatewayDao gatewayDao;

	public void registerUser(String username, String password) {

		AppUser appUser = new AppUser();
		appUser.setUsername(username);
		appUser.setPassword(passwordEncoder.encode(password));
		appUser.setAuthorities("ROLE_USER");

		gatewayDao.createOrUpdateAppUser(appUser);

	}

	public void deRegisteruser(String username) throws UsernameNotFoundException {

		gatewayDao.deleteUser(username);

	}

}
