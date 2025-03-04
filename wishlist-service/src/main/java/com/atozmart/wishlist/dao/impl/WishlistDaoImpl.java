package com.atozmart.wishlist.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Repository;

import com.atozmart.wishlist.dao.WishlistDao;
import com.atozmart.wishlist.dto.ItemDto;
import com.atozmart.wishlist.dto.WishlistDto;
import com.atozmart.wishlist.entity.Wishlist;
import com.atozmart.wishlist.repository.WishlistRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Repository
public class WishlistDaoImpl implements WishlistDao {

	private WishlistRepository wishlistRepo;

	private ModelMapper mapper;

	public List<WishlistDto> getAllByUsername(String username){
		List<Wishlist> wishlists = wishlistRepo.findAllById(Arrays.asList(username));
		
		if(wishlists.isEmpty())
			return new ArrayList<>();
		
		return wishlists.stream().map(wishlist -> mapper.map(wishlist, WishlistDto.class)).toList();
	}

	@Override
	public void addItem(WishlistDto wishlistDto, String username) {
		Wishlist wishlist = mapper.map(wishlistDto, Wishlist.class);
		wishlist.setUsername(username);
		wishlistRepo.save(wishlist);
	}

}
