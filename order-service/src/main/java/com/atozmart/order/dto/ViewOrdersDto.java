package com.atozmart.order.dto;

import java.util.List;

import lombok.Data;

@Data
public class ViewOrdersDto {
	
	private Integer orderId;
	private String paymentStatus;
	private String deliveryStatus;
	private Double orderTotal;
	
	private List<OrderItemsDto> orderItems;

}
