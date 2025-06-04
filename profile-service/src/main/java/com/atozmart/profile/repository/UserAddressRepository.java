package com.atozmart.profile.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.atozmart.profile.entity.UserAddress;
import com.atozmart.profile.entity.UserAddressCompKey;

public interface UserAddressRepository extends JpaRepository<UserAddress, UserAddressCompKey> {

}
