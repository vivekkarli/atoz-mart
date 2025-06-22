package com.atozmart.wishlist.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.atozmart.wishlist.entity.Wishlist;

public interface WishlistRepository extends JpaRepository<Wishlist, Integer> {

	List<Wishlist> findByUsername(String username);
	
	@Modifying
	@Query("delete from Wishlist w where w.username = :username")
	void deleteByUserName(String username);
	
	@Modifying
	@Query("delete from Wishlist w where w.username = :username and w.itemId = :itemId")
	void deleteByUserNameAndItemId(String username, String itemId);

}
