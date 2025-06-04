package com.atozmart.profile.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.atozmart.profile.entity.UserProfile;

public interface UserProfileRepository extends JpaRepository<UserProfile, String> {

}
