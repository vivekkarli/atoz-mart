package com.atozmart.wishlist.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Wishlist {

	@Id
	private String username;
	
	private String itemname;

	private double price;

}
