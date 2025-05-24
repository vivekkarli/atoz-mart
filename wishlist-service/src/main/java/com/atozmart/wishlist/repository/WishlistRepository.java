package com.atozmart.wishlist.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.atozmart.wishlist.entity.Wishlist;

public interface WishlistRepository extends JpaRepository<Wishlist, Integer> {

	List<Wishlist> findByUsername(String username);

}
