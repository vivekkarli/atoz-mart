package com.atozmart.wishlist.util;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.atozmart.wishlist.dto.WishlistDto;
import com.atozmart.wishlist.entity.Wishlist;

@Mapper
public interface WishListMapper {

	WishListMapper INSTANCE = Mappers.getMapper(WishListMapper.class);

	List<WishlistDto> mapWishListsToWishListDtos(List<Wishlist> wishlists);

	Wishlist mapWishListDtoToWishList(WishlistDto wishlistDto);

}
