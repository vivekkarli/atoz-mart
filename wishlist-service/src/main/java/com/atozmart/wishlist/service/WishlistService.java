package com.atozmart.wishlist.service;

import java.util.List;

import com.atozmart.wishlist.dto.ItemDto;
import com.atozmart.wishlist.dto.WishlistDto;
import com.atozmart.wishlist.exception.WishlistException;

public interface WishlistService {

	String addToCart(ItemDto itemDto);

	List<WishlistDto> viewItems(String username) throws WishlistException;

	void addItem(WishlistDto wishlistDto, String username);

}
