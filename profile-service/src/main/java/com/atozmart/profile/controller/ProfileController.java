package com.atozmart.profile.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atozmart.profile.dto.ProfileDetails;
import com.atozmart.profile.exception.ProfileException;
import com.atozmart.profile.service.ProfileDetailsValidator;
import com.atozmart.profile.service.ProfileService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/profile")
@RestController
public class ProfileController {
	
	private final ProfileDetailsValidator profileDetailsValidator;

	private final ProfileService profileService;

	@GetMapping
	public ResponseEntity<ProfileDetails> getProfileDetails(@RequestHeader("X-Username") String username) {
		return ResponseEntity.ok(profileService.getProfileDetails(username));
	}

	@PostMapping
	public ResponseEntity<Void> addNewProfile(@RequestHeader("X-Username") String username,
			@Valid @RequestBody ProfileDetails profileDetails) {
		profileDetailsValidator.validateProfileDetails(profileDetails);
		profileService.addNewProfile(username, profileDetails);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@PatchMapping
	public ResponseEntity<Void> editProfileDetails(@RequestHeader("X-Username") String username,
			@Valid @RequestBody ProfileDetails profileDetails) throws ProfileException {
		profileDetailsValidator.validateProfileDetails(profileDetails);
		profileService.editProfileDetails(username, profileDetails);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@DeleteMapping("/address")
	public ResponseEntity<Void> deleteAddress(@RequestHeader("X-Username") String username,
			@RequestParam(required = false) String addressType) throws ProfileException {
		profileService.deleteAddress(username, addressType);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PatchMapping("/address")
	public ResponseEntity<Void> changeDefault(@RequestHeader("X-Username") String username,
			@RequestParam String addressType) throws ProfileException {
		profileService.changeDefaultTo(username, addressType);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
