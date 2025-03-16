package com.atozmart.wishlist.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.atozmart.wishlist.dto.ItemDto;

@FeignClient(name = "cart-service")
public interface CartFeignClient {

	@PostMapping("/items")
	String addItem(@RequestBody ItemDto item);

}
