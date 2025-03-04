package com.atozmart.wishlist.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.atozmart.wishlist.entity.Wishlist;

public interface WishlistRepository extends JpaRepository<Wishlist, String>{

}
