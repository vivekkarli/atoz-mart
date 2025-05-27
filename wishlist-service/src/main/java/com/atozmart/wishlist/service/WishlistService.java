package com.atozmart.wishlist.service;

import java.util.List;

import com.atozmart.wishlist.dto.WishlistDto;
import com.atozmart.wishlist.exception.WishlistException;

public interface WishlistService {

	List<WishlistDto> viewItems(String username) throws WishlistException;

	void addItem(WishlistDto wishlistDto, String username);
	
	void deleteItems(String username, String itemName);

}
