package com.atozmart.wishlist.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.atozmart.wishlist.dto.cart.ItemDto;

@FeignClient(name = "cart-service")
public interface CartFeignClient {

	@PostMapping("/items")
	String addItem(@RequestHeader("X-Username") String username, @RequestBody ItemDto item);

}
