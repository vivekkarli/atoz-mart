package com.atozmart.profile.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atozmart.profile.dto.ProfileDetails;
import com.atozmart.profile.service.ProfileService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/profile")
@RestController
public class ProfileController {

	private final ProfileService profileService;

	@GetMapping
	public ResponseEntity<ProfileDetails> getProfileDetails(@RequestHeader("X-Username") String username) {
		return ResponseEntity.ok(profileService.getProfileDetails(username));
	}

	@PostMapping
	public ResponseEntity<Void> addNewProfile(@RequestHeader("X-Username") String username,
			@RequestBody ProfileDetails profileDetails) {
		profileService.addNewProfile(username, profileDetails);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@PatchMapping
	public ResponseEntity<ProfileDetails> editProfileDetails(@RequestHeader("X-Username") String username,
			@RequestBody ProfileDetails profileDetails) {
		
		// if username is changed, update the username in auth server also.
		// event
		
		// if default is changed, also update others to false
		// username-addressType-default
		
		return null;
	}

}
