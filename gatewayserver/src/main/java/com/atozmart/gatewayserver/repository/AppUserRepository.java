package com.atozmart.gatewayserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.atozmart.gatewayserver.entity.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, String> {

}
