package com.atozmart.authserver.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.atozmart.authserver.dao.AppUserDao;
import com.atozmart.authserver.dto.AuthorizeResponse;
import com.atozmart.authserver.dto.LoginForm;
import com.atozmart.authserver.dto.LoginResponse;
import com.atozmart.authserver.dto.SignUpForm;
import com.atozmart.authserver.entity.AppRole;
import com.atozmart.authserver.entity.AppUser;
import com.atozmart.authserver.exception.AuthServerException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServerService {

	private final AppUserDao appUserDao;

	private final NotificationService notificationService;
	
	private final ProfileService profileService;

	private final JwtService jwtService;

	private final PasswordEncoder passwordEncoder;

	private final AuthenticationManager authenticationManager;

	public ResponseEntity<LoginResponse> login(LoginForm loginForm)
			throws BadCredentialsException, UsernameNotFoundException {

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

	public ResponseEntity<LoginResponse> signUp(SignUpForm signUpForm) throws AuthServerException {

		AppUser appUser = new AppUser();
		appUser.setUsername(signUpForm.username());
		appUser.setPassword(passwordEncoder.encode(signUpForm.password()));
		appUser.setMail(signUpForm.mail());
		appUser.setMobileNo(signUpForm.mobileNo());
		appUser.setEmailVerified(false);
		appUser.setMobileNoVerified(false);
		appUser.setRoles(Set.of(new AppRole("user", null)));

		log.debug("new appUser: {}", appUser);

		appUserDao.signUp(appUser);
		
		// create new profile, async process
		profileService.createProfile(signUpForm);

		// send email verification mail, async process
		notificationService.sendEmailConfirmationMail(appUser);

		return new ResponseEntity<>(new LoginResponse("signed up successfully"), HttpStatus.ACCEPTED);
	}

	public ResponseEntity<AuthorizeResponse> authorizeToken(String token) throws AuthServerException {

		String username = jwtService.extractUsername(token);
		log.debug("username: {}", username);
		AppUser appUser = appUserDao.loadUserByUsername(username);
		log.debug("userDetails: {}", appUser);

		if (appUser == null)
			throw new AuthServerException(username + " not found", HttpStatus.NOT_FOUND);

		if (jwtService.isTokenExpired(token))
			throw new AuthServerException("token expired", HttpStatus.UNAUTHORIZED);

		Collection<? extends GrantedAuthority> authorities = appUser.getAuthorities();
		List<String> roles = authorities.stream().map(s -> s.getAuthority()).toList();

		AuthorizeResponse response = new AuthorizeResponse();
		response.setValid(true);
		response.setRoles(roles);
		response.setUsername(username);
		response.setEmail(appUser.getMail());
		response.setExpiresAt(jwtService.extractExpiration(token));
		log.debug("AuthorizeResponse: {}", response);

		return new ResponseEntity<>(response, HttpStatus.OK);

	}

	public String getEmail(String username) throws UsernameNotFoundException {
		return appUserDao.loadUserByUsername(username).getMail();
	}

}
