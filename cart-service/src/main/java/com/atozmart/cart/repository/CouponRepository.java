package com.atozmart.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.atozmart.cart.entity.Coupon;

public interface CouponRepository extends JpaRepository<Coupon, String> {

}
