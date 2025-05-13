package com.atozmart.cart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.atozmart.cart.entity.Cart;
import com.atozmart.cart.entity.CartCompositeId;

public interface CartRepository extends JpaRepository<Cart, CartCompositeId> {
	
	List<Cart> findByUsername(String username);
	
	@Modifying
	@Query("delete from Cart c where c.username = :username")
	void deleteByUsername(@Param(value = "username") String username);

}
