package com.atozmart.authserver.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atozmart.authserver.service.AuthServerService;

import lombok.AllArgsConstructor;

@RequestMapping("/admin")
@RestController
@AllArgsConstructor
public class AuthServerAdminController {
	
	private AuthServerService authServerService;
	
	/**
	 * to find email id of the user.
	 * @param username
	 * @return String email id
	 */
	@GetMapping("/email/{username}")
	public ResponseEntity<String> getEmail(@PathVariable String username) {
		return ResponseEntity.ok(authServerService.getEmail(username));
	}

}
