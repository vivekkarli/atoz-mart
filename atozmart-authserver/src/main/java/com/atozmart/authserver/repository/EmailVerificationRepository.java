package com.atozmart.authserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.atozmart.authserver.entity.EmailVerification;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, String>{

}
