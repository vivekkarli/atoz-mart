package com.atozmart.profile.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.atozmart.profile.dto.BasicDetails;

@FeignClient(name = "atozmart-authserver")
public interface AuthServerFeignClient {

	// PUT instead of PATCH, since open feign doesn't support PATCH out of the box
	@PutMapping("/admin/profile")
	public ResponseEntity<Void> updateBasicDetails(@RequestHeader("X-Username") String username,
			@RequestBody BasicDetails basicDetails);

}
