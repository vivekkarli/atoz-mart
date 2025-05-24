package com.atozmart.wishlist.service.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.atozmart.commons.exception.dto.DownStreamException;
import com.atozmart.wishlist.dao.WishlistDao;
import com.atozmart.wishlist.dto.WishlistDto;
import com.atozmart.wishlist.dto.cart.ItemDto;
import com.atozmart.wishlist.exception.WishlistException;
import com.atozmart.wishlist.service.CartFeignClient;
import com.atozmart.wishlist.service.WishlistService;

import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class WishlistServiceImpl implements WishlistService {

	private CartFeignClient cartFeignClient;
	
	private WishlistDao wishlistDao;
	
	@Override
	public String addToCart(String username, ItemDto itemDto) {
		log.info("method: addToCart");
		try {
		return cartFeignClient.addItem(username, itemDto);
		} catch (FeignException e) {
			throw new DownStreamException(e.contentUTF8(), HttpStatus.valueOf(e.status()));
		}
	}

	@Override
	public List<WishlistDto> viewItems(String username) throws WishlistException {
		List<WishlistDto> wishlistDtos = wishlistDao.getAllByUsername(username);
		
		if(wishlistDtos.isEmpty())
			throw new WishlistException("no items in wishlist", HttpStatus.NOT_FOUND);
		
		return wishlistDtos;
	}

	@Override
	public void addItem(WishlistDto wishlistDto, String username) {
		wishlistDao.addItem(wishlistDto, username);
		
	}
	
	
	
}
