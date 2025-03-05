package com.atozmart.gatewayserver.controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atozmart.gatewayserver.service.GatewayService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@AllArgsConstructor
public class GatewayController {

	private final GatewayService gatewayService;

	@PostMapping("/signup")
	public ResponseEntity<String> registerUser(@RequestParam String username, @RequestParam String password) {

		gatewayService.registerUser(username, password);

		return new ResponseEntity<>("user created", HttpStatus.CREATED);

	}

	@DeleteMapping("/remove-account")
	public ResponseEntity<String> deRegisteruser(Principal principal) throws UsernameNotFoundException {
		
		if(principal == null)
			throw new UsernameNotFoundException("no logged-in user found. User need to be logged-in to delete his/her account");
		
		String username = principal.getName();
		log.debug("logged in user: {}", username);
		
		gatewayService.deRegisteruser(username);

		return new ResponseEntity<>("user deleted", HttpStatus.OK);
	}

}
