package com.atozmart.cart.dao;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import com.atozmart.cart.entity.Coupon;
import com.atozmart.cart.exception.CartException;
import com.atozmart.cart.exception.CouponException;
import com.atozmart.cart.repository.CouponRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CouponDao {

	private final CouponRepository couponRepo;

	public double getCouponDiscount(String couponCode) {
		Coupon coupon = couponRepo.findById(couponCode)
				.orElseThrow(() -> new CartException("coupon code not found", HttpStatus.BAD_REQUEST));

		return coupon.getDiscount();
	}

	public List<Coupon> getAllCoupons() {
		List<Coupon> couponList = couponRepo.findAll();
		if (couponList.isEmpty())
			throw new CouponException("No coupons available at the moment", HttpStatus.NOT_FOUND);

		return couponList;
	}

}
