package com.atozmart.cart.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Coupon {
	
	@Id
	private String couponCode;
	private double discount;

}
