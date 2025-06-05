package com.atozmart.profile.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.atozmart.profile.entity.UserAddress;
import com.atozmart.profile.entity.UserAddressCompKey;

public interface UserAddressRepository extends JpaRepository<UserAddress, UserAddressCompKey> {

	@Modifying
	@Query("""
			delete 
			from
			  UserAddress u
			 where 
			   u.username = :username
			""")
	void deleteByUsername(String username);

	Optional<UserAddress> findByUsernameAndDefaultAddress(String username, boolean defaultAddress);

}
