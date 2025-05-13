package com.atozmart.order.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "atozmart-authserver")
public interface AuthServerFeignClient {
	
	@GetMapping("/admin/email/{username}")
	public ResponseEntity<String> getEmail(@PathVariable String username);

}
