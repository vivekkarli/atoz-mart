package com.atozmart.gatewayserver.controller;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atozmart.gatewayserver.dto.FallBackResponse;

@RestController
public class GatewayController {

	@GetMapping("/test")
	public String testEndpoint() {
		return "test";
	}

	@RequestMapping("/fallback/authserver")
	public ResponseEntity<FallBackResponse> authserverFallback() {
		return ResponseEntity.status(503).body(new FallBackResponse(
				"authserver not responding, please try again after some time", LocalDateTime.now()));
	}

}
