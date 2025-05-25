package com.atozmart.wishlist.dao;

import java.util.List;

import com.atozmart.wishlist.dto.WishlistDto;

public interface WishlistDao {

	List<WishlistDto> getAllByUsername(String username);
	
	void addItem(WishlistDto wishlistDto, String username);
	
	void deleteItems(String username, String itemName);
	
}
