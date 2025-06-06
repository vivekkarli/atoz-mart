package com.atozmart.cart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.atozmart.cart.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, Integer> {

	List<Cart> findByUsername(String username);
	
	Optional<Cart> findByUsernameAndItemName(String username, String itemName);

	@Modifying
	@Query("delete from Cart c where c.username = :username")
	void deleteByUsername(@Param(value = "username") String username);

	@Modifying
	@Query("delete from Cart c where c.username = :username and c.itemName = :itemName")
	void deleteByUsernameAndItemName(@Param("username") String username, @Param("itemName") String itemName);

}
