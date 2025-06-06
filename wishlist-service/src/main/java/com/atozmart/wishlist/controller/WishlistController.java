package com.atozmart.wishlist.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atozmart.wishlist.dto.WishlistDto;
import com.atozmart.wishlist.exception.WishlistException;
import com.atozmart.wishlist.service.WishlistService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class WishlistController {

	private final WishlistService wishlistService;

	@PostMapping("/items")
	public ResponseEntity<String> addItem(@RequestHeader("X-Username") String username,
			@RequestBody WishlistDto wishlistDto) {
		log.info("added ietms into wishlist");
		wishlistService.addItem(wishlistDto, username);
		return new ResponseEntity<>("added", HttpStatus.CREATED);

	}

	@GetMapping("/items")
	public ResponseEntity<List<WishlistDto>> viewItems(@RequestHeader("X-Username") String username)
			throws WishlistException {
		log.info("X-Username: {}",username);
		log.info("showing items from wishlist");
		List<WishlistDto> wishlistDtos = wishlistService.viewItems(username);
		return ResponseEntity.ok(wishlistDtos);

	}

	@DeleteMapping("/items")
	public ResponseEntity<Void> removeItem(@RequestHeader("X-Username") String username, @RequestParam(required = false) String itemName) {
		wishlistService.deleteItems(username, itemName);
		return ResponseEntity.noContent().build();

	}

}
