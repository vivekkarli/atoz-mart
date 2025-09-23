package com.atozmart.authserver.dao;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import com.atozmart.authserver.dto.profile.BasicDetailsDto;
import com.atozmart.authserver.entity.AppUser;
import com.atozmart.authserver.exception.AuthServerException;
import com.atozmart.authserver.repository.AppUserRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AppUserDao implements UserDetailsService {

	private final AppUserRepository appUserRepository;

	private final PasswordEncoder passwordEncoder;

	@Override
	public AppUser loadUserByUsername(String username) throws UsernameNotFoundException {
		return appUserRepository.findById(username).orElseThrow(() -> new UsernameNotFoundException("user not found"));
	}

	public void signUp(AppUser appUser) throws AuthServerException {
		if (appUserRepository.existsById(appUser.getUsername()))
			throw new AuthServerException("user already exists", HttpStatus.BAD_REQUEST);
		appUserRepository.save(appUser);
	}

	public void updatePassword(String username, String oldPassword, String newPassword) throws AuthServerException {
		AppUser appUser = appUserRepository.findById(username)
				.orElseThrow(() -> new UsernameNotFoundException("user not found"));

		if (!passwordEncoder.matches(oldPassword, appUser.getPassword())) {
			throw new AuthServerException("Incorrect old password", HttpStatus.UNAUTHORIZED);
		}

		appUser.setPassword(passwordEncoder.encode(newPassword));
		appUserRepository.save(appUser);
	}

	public void updateBasicDetails(String username, BasicDetailsDto basicDetailsDto) {

		AppUser appUser = loadUserByUsername(username);
		appUser.setMail(basicDetailsDto.mail());
		appUser.setMobileNo(basicDetailsDto.mobileNo());

	}

}
