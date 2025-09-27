package com.atozmart.order.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ViewOrdersDto(

		Integer orderId, String paymentStatus, String deliveryStatus, String orderStatus, Double orderTotal,
		LocalDateTime orderedOn,

		List<OrderItemDto> orderItems) {

}
