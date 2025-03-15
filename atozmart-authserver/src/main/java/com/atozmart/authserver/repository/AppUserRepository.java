package com.atozmart.authserver.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.atozmart.authserver.entity.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, String> {
	
	Optional<AppUser> findByUsername(String username);

}
