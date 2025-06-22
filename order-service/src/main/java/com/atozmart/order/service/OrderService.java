package com.atozmart.order.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.atozmart.order.dao.OrdersDao;
import com.atozmart.order.dto.OrderItemDto;
import com.atozmart.order.dto.PlaceOrderRequest;
import com.atozmart.order.dto.PlaceOrderResponse;
import com.atozmart.order.dto.ViewOrdersDto;
import com.atozmart.order.entity.Orders;
import com.atozmart.order.exception.OrderException;
import com.atozmart.order.util.OrdersMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

	private final OrdersDao ordersDao;

	private final OrderServiceHelper orderServiceHelper;

	public PlaceOrderResponse placeOrder(String username, String email, PlaceOrderRequest placeOrderRequest)
			throws OrderException {
		String orderId = ordersDao.placeOrder(username, placeOrderRequest);
		orderServiceHelper.decrementStock(placeOrderRequest.items());
		orderServiceHelper.sendEmailNotification(username, email, orderId);
		return new PlaceOrderResponse(orderId, "success");
	}

	public List<ViewOrdersDto> getOrderDetails(String username, Integer orderId) {

		List<Orders> orderDetails = ordersDao.getOrderDetails(username, orderId);
		log.info("orderDetails: {}", orderDetails);

		List<ViewOrdersDto> viewOrdersDtos = new ArrayList<>();

		orderDetails.forEach(order -> {
			if (orderId == null) {
				ViewOrdersDto viewOrdersDto = new ViewOrdersDto(order.getOrderId(), order.getPaymentStatus(),
						order.getDeliveryStatus(), order.getOrderStatus(), order.getOrderTotal(), null);

				viewOrdersDtos.add(viewOrdersDto);
			} else {
				List<OrderItemDto> orderItemDtos = 
						OrdersMapper.INSTANCE.orderItemsToOrderItemsDto(new ArrayList<>(order.getOrderItems()));

				ViewOrdersDto viewOrdersDto = new ViewOrdersDto(order.getOrderId(), order.getPaymentStatus(),
						order.getDeliveryStatus(), order.getOrderStatus(), order.getOrderTotal(), orderItemDtos);

				viewOrdersDtos.add(viewOrdersDto);
			}
		});

		return viewOrdersDtos;

	}

	public void cancelOrder(String username, Integer orderId) {
		Orders cancelledOrder = ordersDao.changeOrderStatus(username, orderId, "cancelled by customer");

		List<OrderItemDto> orderItemDtos = new ArrayList<>();

		cancelledOrder.getOrderItems()
				.forEach(orderItem -> orderItemDtos.add(new OrderItemDto(orderItem.getItemId(), orderItem.getItemName(),
						orderItem.getUnitPrice(), orderItem.getQuantity(), orderItem.getEffectivePrice())));

		orderServiceHelper.restoreStockAsync(orderItemDtos);

	}

}
