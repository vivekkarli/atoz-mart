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
import com.atozmart.order.util.OrderConstants;
import com.atozmart.order.util.OrdersMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

	private final OrdersDao ordersDao;

	private final OrderServiceHelper orderServiceHelper;

	public PlaceOrderResponse placeOrder(PlaceOrderRequest placeOrderRequest, String username, String email)
			throws OrderException {

		String orderId = ordersDao.placeOrder(username, placeOrderRequest);

		orderServiceHelper.decrementStockAsync(placeOrderRequest.items());
		orderServiceHelper.sendEmailNotificationAsync(username, email,
				OrderConstants.getOrderPlacedMailContent(orderId));

		return new PlaceOrderResponse(orderId, "success");
	}

	public List<ViewOrdersDto> getOrderDetails(Integer orderId, String username) {

		List<Orders> orders = ordersDao.getOrderDetails(username, orderId);
		log.info("orderDetails: {}", orders);

		return mapToViewOrdersDto(orderId, orders);

	}

	public void cancelOrder(Integer orderId, String username, String email) {
		Orders cancelledOrder = ordersDao.changeOrderStatus(username, orderId, "cancelled by customer");

		List<OrderItemDto> orderItemDtos = new ArrayList<>();

		cancelledOrder.getOrderItems()
				.forEach(orderItem -> orderItemDtos.add(new OrderItemDto(orderItem.getItemId(), orderItem.getItemName(),
						orderItem.getUnitPrice(), orderItem.getQuantity(), orderItem.getEffectivePrice())));

		orderServiceHelper.restoreStockAsync(orderItemDtos);
		orderServiceHelper.sendEmailNotificationAsync(username, email,
				OrderConstants.getOrderCancelMailContent(orderId, cancelledOrder.getOrderTotal()));

	}

	private List<ViewOrdersDto> mapToViewOrdersDto(Integer orderId, List<Orders> orders) {

		List<ViewOrdersDto> viewOrdersDtos = new ArrayList<>();

		orders.forEach(order -> {
			if (orderId == null) {
				ViewOrdersDto viewOrdersDto = new ViewOrdersDto(order.getOrderId(), order.getPaymentStatus(),
						order.getDeliveryStatus(), order.getOrderStatus(), order.getOrderTotal(), order.getCreatedAt(), null);

				viewOrdersDtos.add(viewOrdersDto);
			} else {
				List<OrderItemDto> orderItemDtos = OrdersMapper.INSTANCE
						.orderItemsToOrderItemsDto(new ArrayList<>(order.getOrderItems()));

				ViewOrdersDto viewOrdersDto = new ViewOrdersDto(order.getOrderId(), order.getPaymentStatus(),
						order.getDeliveryStatus(), order.getOrderStatus(), order.getOrderTotal(), order.getCreatedAt(), orderItemDtos);

				viewOrdersDtos.add(viewOrdersDto);
			}
		});

		return viewOrdersDtos;
	}

}
