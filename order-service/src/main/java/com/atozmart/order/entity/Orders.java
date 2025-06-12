package com.atozmart.order.entity;

import java.time.LocalDateTime;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@ToString(exclude = { "orderItems" })
@EqualsAndHashCode(exclude = { "orderItems" })
@SequenceGenerator(name = "orders_seq", sequenceName = "orders_seq", initialValue = 10000000, allocationSize = 1)
public class Orders {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orders_seq")
	private Integer orderId;
	private String username;
	private String paymentMode;
	private String paymentStatus;
	private String deliveryStatus;
	private Double orderTotal;

	@OneToMany(mappedBy = "orderId", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<OrderItem> orderItems;

	@CreationTimestamp
	@Column(updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(insertable = false)
	private LocalDateTime updatedAt;

}
