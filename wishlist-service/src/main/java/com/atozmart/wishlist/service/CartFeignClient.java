package com.atozmart.wishlist.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.atozmart.wishlist.dto.ItemDto;

@FeignClient(url = "localhost:8081", name = "CartFeignClient")
public interface CartFeignClient {

	@PostMapping("/items")
	String addItem(@RequestBody ItemDto item);

}
