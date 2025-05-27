package com.atozmart.wishlist.service.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.atozmart.wishlist.dao.WishlistDao;
import com.atozmart.wishlist.dto.WishlistDto;
import com.atozmart.wishlist.exception.WishlistException;
import com.atozmart.wishlist.service.WishlistService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

	private final WishlistDao wishlistDao;

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

	@Override
	public void deleteItems(String username, String itemName) {
		wishlistDao.deleteItems(username, itemName);		
	}
	
	
	
}
