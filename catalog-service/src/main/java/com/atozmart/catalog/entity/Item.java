package com.atozmart.catalog.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Data
@Entity
@DynamicUpdate
@DynamicInsert
public class Item {

	@Id
	private String id;

	private String name;

	private Double unitPrice;
	
	private String details;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category")
	private Category category;
	
	@OneToOne(mappedBy = "item", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Inventory inventory;
	
	@CreationTimestamp
	@Column(updatable = false)
	private LocalDateTime createdAt;
	
	@UpdateTimestamp
	@Column(insertable = false)
	private LocalDateTime updatedAt;

	@Override
	public String toString() {
		return "Item [id=" + id + ", name=" + name + ", unitPrice=" + unitPrice + ", details=" + details + ", category="
				+ category.getName() + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
	}
	
	

}
