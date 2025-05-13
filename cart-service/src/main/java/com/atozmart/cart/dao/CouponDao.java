package com.atozmart.cart.dao;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import com.atozmart.cart.entity.Coupon;
import com.atozmart.cart.exception.CartException;
import com.atozmart.cart.repository.CouponRepository;

import lombok.AllArgsConstructor;

@Repository
@AllArgsConstructor
public class CouponDao {

	private CouponRepository couponRepo;

	public double getCouponDiscount(String couponCode) {
		Coupon coupon = couponRepo.findById(couponCode)
				.orElseThrow(() -> new CartException("coupon code not found", HttpStatus.BAD_REQUEST));
				
		return coupon.getDiscount();
	}

}
