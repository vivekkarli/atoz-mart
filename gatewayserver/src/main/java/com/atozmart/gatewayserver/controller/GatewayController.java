package com.atozmart.gatewayserver.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GatewayController {
	
	
	@GetMapping("/test")
	public String testEndpoint() {
		return "test";
	}

}
