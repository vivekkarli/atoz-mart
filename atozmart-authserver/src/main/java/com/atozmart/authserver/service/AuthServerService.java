package com.atozmart.authserver.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.atozmart.authserver.dao.AppUserDao;
import com.atozmart.authserver.dto.AuthorizeResponse;
import com.atozmart.authserver.dto.LoginForm;
import com.atozmart.authserver.dto.SignUpForm;
import com.atozmart.authserver.entity.AppUser;
import com.atozmart.authserver.exception.AuthServerException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class AuthServerService {

	private AppUserDao appUserDao;

	private JwtService jwtService;

	private PasswordEncoder passwordEncoder;
	
	public String login(LoginForm loginForm) {
		AppUser appUser = (AppUser) appUserDao.loadUserByUsername(loginForm.username());

		if (appUser == null || !passwordEncoder.matches(loginForm.password(), appUser.getPassword()))
			throw new BadCredentialsException("invalid credentials");

		List<String> roles = appUser.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
		Map<String, Object> customClaims = new HashMap<>();
		customClaims.put("preferred_username", appUser.getUsername());
		customClaims.put("roles", roles);

		return jwtService.generateToken(appUser, customClaims);

	}
	
	public void signUp(SignUpForm signUpForm) {
		
		AppUser appUser = new AppUser();
		appUser.setUsername(signUpForm.username());
		appUser.setPassword(passwordEncoder.encode(signUpForm.password()));
		appUser.setMail(signUpForm.mail());
		appUser.setMobileNo(signUpForm.mobileNo());
		appUser.setRoles("USER");
		
		log.debug("appUser: {}", appUser);
		
		appUserDao.signUp(appUser);
	}

	public AuthorizeResponse authorizeToken(String token) {

		String username = jwtService.extractUsername(token);
		log.debug("username: {}", username);
		UserDetails userDetails = appUserDao.loadUserByUsername(username);
		log.debug("userDetails: {}", userDetails);
		
		if (userDetails == null)
			throw new AuthServerException(username + " not found");

		if (jwtService.isTokenExpired(token))
			throw new AuthServerException("token expired");

		Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
		List<String> roles = authorities.stream().map(s -> s.getAuthority()).toList();
		
		AuthorizeResponse response = new AuthorizeResponse();
		response.setValid(true);
		response.setRoles(roles);
		response.setUsername(username);
		response.setExpiresAt(jwtService.extractExpiration(token));
		log.debug("AuthorizeResponse: {}", response);
		return response;

	}

}
