package com.atozmart.order.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class OrderItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "order_id", nullable = false)
	private Orders orderId;
	
	private String itemId;
	
	private String itemName;

	private double unitPrice;

	private int quantity;

	private double effectivePrice;

	@CreationTimestamp
	@Column(updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(insertable = false)
	private LocalDateTime updatedAt;

	@Override
	public String toString() {
		return "OrderItem [itemId=" + itemId + ", itemName=" + itemName + ", orderId=" + orderId.getOrderId() + ", unitPrice="
				+ unitPrice + ", quantity=" + quantity + ", effectivePrice=" + effectivePrice + ", createdAt="
				+ createdAt + ", updatedAt=" + updatedAt + "]";
	}

	
	

}
