package com.atozmart.catalog.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Item {
	
	@Id
	private Integer id;
	
	private String name;
	
	private String price;

}
