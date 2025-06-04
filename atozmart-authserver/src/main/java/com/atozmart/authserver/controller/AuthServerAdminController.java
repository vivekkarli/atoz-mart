package com.atozmart.authserver.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atozmart.authserver.dto.profile.BasicDetails;
import com.atozmart.authserver.service.AuthServerService;
import com.atozmart.authserver.service.ProfileService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/admin")
@RestController
@RequiredArgsConstructor
public class AuthServerAdminController {

	private final AuthServerService authServerService;

	private final ProfileService profileService;

	/**
	 * to find email id of the user.
	 * 
	 * @param username
	 * @return String email id
	 */
	@GetMapping("/email/{username}")
	public ResponseEntity<String> getEmail(@PathVariable String username) {
		return ResponseEntity.ok(authServerService.getEmail(username));
	}

	// PUT instead of PATCH, since open feign doesn't support PATCH out of the box
	@PutMapping("/profile")
	public ResponseEntity<Void> updateBasicDetails(@RequestHeader("X-Username") String username,
			@RequestBody BasicDetails basicDetails) {
		log.info("X-Username: {}", username);
		profileService.updateBasicDetails(username, basicDetails);
		return ResponseEntity.ok().build();
	}

}
