package com.atozmart.order.dto;

import java.util.List;

public record ViewOrdersDto(

		Integer orderId, String paymentStatus, String deliveryStatus, String orderStatus, Double orderTotal,

		List<OrderItemDto> orderItems) {

}
