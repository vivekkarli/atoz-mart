package com.atozmart.cart.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.atozmart.cart.dao.CouponDao;
import com.atozmart.cart.dto.CouponResponse;
import com.atozmart.cart.entity.Coupon;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouponService {

	private final CouponDao couponDao;

	public List<CouponResponse> getAvailableCoupons() {
		List<Coupon> couponList = couponDao.getAllCoupons();

		return couponList.stream()
				.map(coupon -> new CouponResponse(coupon.getCouponCode(), coupon.getDiscount()))
				.toList();
	}

}
