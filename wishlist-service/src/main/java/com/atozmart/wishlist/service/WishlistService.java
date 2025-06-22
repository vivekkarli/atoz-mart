package com.atozmart.wishlist.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.atozmart.wishlist.dao.WishlistDao;
import com.atozmart.wishlist.dto.WishlistDto;
import com.atozmart.wishlist.entity.Wishlist;
import com.atozmart.wishlist.exception.WishlistException;
import com.atozmart.wishlist.util.WishListMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WishlistService {

	private final WishlistDao wishlistDao;

	public List<WishlistDto> viewItems(String username) throws WishlistException {
		List<Wishlist> wishlists = wishlistDao.getAllByUsername(username);
		return WishListMapper.INSTANCE.mapWishListsToWishListDtos(wishlists);
	}

	public void addItem(WishlistDto wishlistDto, String username) {
		
		Wishlist wishList = WishListMapper.INSTANCE.mapWishListDtoToWishList(wishlistDto);
		wishlistDao.addItem(wishList, username);
		
	}

	public void deleteItems(String username, String itemId) {
		wishlistDao.deleteItems(username, itemId);		
	}
	
	
	
}
