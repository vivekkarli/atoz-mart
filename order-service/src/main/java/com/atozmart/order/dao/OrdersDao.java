package com.atozmart.order.dao;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.atozmart.order.dto.PlaceOrderRequest;
import com.atozmart.order.entity.OrderItem;
import com.atozmart.order.entity.Orders;
import com.atozmart.order.exception.OrderException;
import com.atozmart.order.repository.OrdersRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@AllArgsConstructor
public class OrdersDao {

	private OrdersRepository orderRepo;

	private ModelMapper mapper;

	public String placeOrder(String username, PlaceOrderRequest placeOrderRequest) throws OrderException {

		Orders orders = new Orders();
		orders.setUsername(username);
		orders.setPaymentMode(placeOrderRequest.paymentMode());
		orders.setPaymentStatus("completed");
		orders.setDeliveryStatus("arriving by tommorow");
		orders.setOrderStatus("accepted");
		orders.setOrderTotal(placeOrderRequest.orderTotal());

		Set<OrderItem> orderItems = placeOrderRequest.items().stream().map(item -> {
			OrderItem orderItem = mapper.map(item, OrderItem.class);
			orderItem.setOrderId(orders);
			return orderItem;
		}).collect(Collectors.toSet());

		orders.setOrderItems(orderItems);

		log.debug("orderItems: {}", orderItems);
		log.debug("orders: {}", orders);

		Orders savedOrder = orderRepo.save(orders);

		return savedOrder.getOrderId().toString();
	}

	public List<Orders> getOrderDetails(String username, Integer orderId) throws OrderException {

		if (orderId == null) {
			List<Orders> orders = orderRepo.findByUsername(username);

			if (orders.isEmpty())
				throw new OrderException("no orders found", HttpStatus.NOT_FOUND);

			return orders;
		}

		Orders order = orderRepo.findByUsernameAndOrderId(username, orderId).orElseThrow(
				() -> new OrderException("order with order id: %d not found".formatted(orderId), HttpStatus.NOT_FOUND));
		Set<OrderItem> orderItems = order.getOrderItems();
		order.setOrderItems(orderItems);

		return List.of(order);

	}

	@Transactional
	public void changeOrderStatus(String username, Integer orderId, String orderStatus) throws OrderException {
		Orders order = orderRepo.findByUsernameAndOrderId(username, orderId).orElseThrow(
				() -> new OrderException("order with order id: %d not found".formatted(orderId), HttpStatus.NOT_FOUND));
		order.setOrderStatus(orderStatus);
	}

}
