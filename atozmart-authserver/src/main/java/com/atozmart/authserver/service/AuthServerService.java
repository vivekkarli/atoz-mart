package com.atozmart.authserver.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.atozmart.authserver.cache.CacheHelper;
import com.atozmart.authserver.dao.AppUserDao;
import com.atozmart.authserver.dto.AppUserDto;
import com.atozmart.authserver.dto.AuthorizeResponse;
import com.atozmart.authserver.dto.ChangePasswordRequest;
import com.atozmart.authserver.dto.LoginForm;
import com.atozmart.authserver.dto.LoginResponse;
import com.atozmart.authserver.dto.SignUpForm;
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

	private final CacheHelper appUserCacheHelper;

	private static final String CACHE_PREFIX;

	static {
		CACHE_PREFIX = "app-user::";
	}

	public ResponseEntity<LoginResponse> login(LoginForm loginForm)
			throws BadCredentialsException, UsernameNotFoundException {

		AppUserDto appUserDto = appUserDao.getUser(loginForm.username());
		try {
			if (!passwordEncoder.matches(loginForm.password(), appUserDto.getPassword())) {
				throw new AuthServerException("username or password is incorrect", HttpStatus.UNAUTHORIZED);
			}
		} catch (UsernameNotFoundException e) {
			log.debug(e.getMessage());
			throw new AuthServerException("username or password is incorrect", HttpStatus.UNAUTHORIZED);
		}

		log.info("authenticated user: {}", appUserDto);

		Map<String, Object> customClaims = new HashMap<>();
		customClaims.put("preferred_username", appUserDto.getUsername());
		customClaims.put("roles", appUserDto.getRoles());
		customClaims.put("email", Boolean.TRUE.equals(appUserDto.getEmailVerified()) ? appUserDto.getMail() : null);

		String accessToken = jwtService.generateToken(appUserDto, customClaims);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("X-Access-Token", accessToken);

		return new ResponseEntity<>(new LoginResponse("logged in successfully"), httpHeaders, HttpStatus.OK);
	}

	public ResponseEntity<LoginResponse> signUp(SignUpForm signUpForm) throws AuthServerException {

		AppUserDto appUserDto = new AppUserDto();
		appUserDto.setUsername(signUpForm.username());
		appUserDto.setPassword(passwordEncoder.encode(signUpForm.password()));
		appUserDto.setMail(signUpForm.mail());
		appUserDto.setMobileNo(signUpForm.mobileNo());
		appUserDto.setEmailVerified(false);
		appUserDto.setMobileNoVerified(false);
		appUserDto.setRoles(Set.of("user"));

		log.info("new appUser: {}", appUserDto);
		appUserDao.createUser(appUserDto);

		/*
		 * cache the new user
		 */
		appUserCacheHelper.cachePut(CACHE_PREFIX + appUserDto.getUsername(), appUserDto);

		// create new profile, async process
		profileService.createProfileAsync(signUpForm);

		// send email verification mail, async process
		notificationService.sendNewMailVerifiyLinkAsync(signUpForm.username(), signUpForm.mail());

		return new ResponseEntity<>(new LoginResponse("signed up successfully"), HttpStatus.ACCEPTED);
	}

	public ResponseEntity<AuthorizeResponse> authorizeToken(String token) {

		if (jwtService.isTokenExpired(token))
			throw new AuthServerException("token expired", HttpStatus.UNAUTHORIZED);
		
		String username = jwtService.extractUsername(token);
		log.info("username: {}", username);

		AppUserDto appUserDto = appUserDao.getUser(username);
		log.info("appUser: {}", appUserDto);

		List<String> roles = appUserDto.getRoles().stream().toList();

		AuthorizeResponse response = new AuthorizeResponse();
		response.setValid(true);
		response.setRoles(roles);
		response.setUsername(username);
		response.setEmail(Boolean.TRUE.equals(appUserDto.getEmailVerified()) ? appUserDto.getMail() : null);
		response.setExpiresAt(jwtService.extractExpiration(token));
		log.info("AuthorizeResponse: {}", response);

		return new ResponseEntity<>(response, HttpStatus.OK);

	}

	public String getEmail(String username) throws UsernameNotFoundException {
		AppUserDto appUserDto = appUserDao.getUser(username);

		if (Boolean.FALSE.equals(appUserDto.getEmailVerified())) {
			log.info("email not verified by user: {}", username);
			return null;
		}
		return appUserDto.getMail();
	}

	public void changePassword(String username, ChangePasswordRequest request) {
		AppUserDto appUserDto = appUserDao.getUser(username);
		if (!passwordEncoder.matches(request.oldPassword(), appUserDto.getPassword())) {
			throw new AuthServerException("Incorrect old password", HttpStatus.UNAUTHORIZED);
		}

		appUserDto.setPassword(passwordEncoder.encode(request.newPassword()));
		appUserDao.updateUser(appUserDto);
		
		/*
		 * cache the updated user
		 */
		appUserCacheHelper.cachePut(CACHE_PREFIX + username, appUserDto);
	}

}
