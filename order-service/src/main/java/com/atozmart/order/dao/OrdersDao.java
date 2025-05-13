package com.atozmart.order.dao;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Repository;

import com.atozmart.order.dto.PlaceOrderRequest;
import com.atozmart.order.entity.OrderItem;
import com.atozmart.order.entity.Orders;
import com.atozmart.order.repository.OrdersRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@AllArgsConstructor
public class OrdersDao {

	private OrdersRepository orderRepo;

	private ModelMapper mapper;

	public String placeOrder(String username, PlaceOrderRequest placeOrderRequest) {

		Orders orders = new Orders();
		orders.setUsername(username);
		orders.setPaymentMode(placeOrderRequest.paymentMode());
		orders.setPaymentStatus("completed");
		orders.setDeliveryStatus("arriving by tommorow");

		List<OrderItem> orderItems = placeOrderRequest.items().stream().map(item -> {
			OrderItem orderItem = mapper.map(item, OrderItem.class);
			orderItem.setOrderId(orders);
			return orderItem;
		}).toList();

		orders.setOrderItems(orderItems);

		log.debug("orderItems: {}", orderItems);
		log.debug("orders: {}", orders);

		Orders savedOrder = orderRepo.save(orders);

		return savedOrder.getOrderId().toString();
	}

}
