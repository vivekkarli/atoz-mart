package com.atozmart.authserver.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.atozmart.authserver.dao.AppUserDao;
import com.atozmart.authserver.dto.AuthorizeResponse;
import com.atozmart.authserver.dto.LoginForm;
import com.atozmart.authserver.dto.LoginResponse;
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

	private NotificationService notificationService;

	private JwtService jwtService;

	private PasswordEncoder passwordEncoder;

	private final AuthenticationManager authenticationManager;

	public ResponseEntity<LoginResponse> login(LoginForm loginForm) throws BadCredentialsException {

		Authentication authenticateduser = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(loginForm.username(), loginForm.password()));

		log.info("{}", authenticateduser);
		AppUser appUser = (AppUser) authenticateduser.getPrincipal();

		List<String> roles = appUser.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
		Map<String, Object> customClaims = new HashMap<>();
		customClaims.put("preferred_username", appUser.getUsername());
		customClaims.put("roles", roles);

		String accessToken = jwtService.generateToken(appUser, customClaims);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("X-Access-Token", accessToken);

		return new ResponseEntity<>(new LoginResponse("logged in successfully"), httpHeaders, HttpStatus.OK);
	}

	public ResponseEntity<LoginResponse> signUp(SignUpForm signUpForm) {

		AppUser appUser = new AppUser();
		appUser.setUsername(signUpForm.username());
		appUser.setPassword(passwordEncoder.encode(signUpForm.password()));
		appUser.setMail(signUpForm.mail());
		appUser.setMobileNo(signUpForm.mobileNo());
		appUser.setRoles("ROLE_USER");
		appUser.setEmailVerified(false);
		appUser.setMobileNoVerified(false);

		log.debug("new appUser: {}", appUser);

		appUserDao.signUp(appUser);

		// async process
		notificationService.sendEmailVerificationMail(appUser);

		return new ResponseEntity<>(new LoginResponse("signed up successfully"), HttpStatus.CREATED);
	}

	public ResponseEntity<AuthorizeResponse> authorizeToken(String token) {

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

		return new ResponseEntity<>(response, HttpStatus.OK);

	}

}
