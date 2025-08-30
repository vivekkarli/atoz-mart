package com.atozmart.order.dao;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.atozmart.order.dto.PlaceOrderRequest;
import com.atozmart.order.entity.OrderItem;
import com.atozmart.order.entity.Orders;
import com.atozmart.order.exception.OrderException;
import com.atozmart.order.repository.OrdersRepository;
import com.atozmart.order.util.OrderConstants;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@AllArgsConstructor
public class OrdersDao {

	private final OrdersRepository orderRepo;

	@Transactional
	public String placeOrder(String username, PlaceOrderRequest placeOrderRequest) throws OrderException {

		Orders orders = new Orders();
		orders.setUsername(username);
		orders.setPaymentMode(placeOrderRequest.paymentMode());
		orders.setPaymentStatus("completed");
		orders.setDeliveryStatus("arriving by tommorow");
		orders.setOrderStatus("accepted");
		orders.setOrderTotal(placeOrderRequest.orderTotal());

		// List<OrderItem> orderItems =
		// OrdersMapper.INSTANCE.orderItemsDtoToViewOrderItems(placeOrderRequest.items());

		Set<OrderItem> orderItems = placeOrderRequest.items().stream().map(item -> {
			OrderItem orderItem = new OrderItem();
			orderItem.setItemId(item.itemId());
			orderItem.setItemName(item.itemName());
			orderItem.setQuantity(item.quantity());
			orderItem.setUnitPrice(item.unitPrice());
			orderItem.setEffectivePrice(item.effectivePrice());
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
				throw new OrderException(OrderConstants.ORDER_NOT_FOUND, HttpStatus.NOT_FOUND);

			return orders;
		}

		Orders orders = orderRepo.findByUsernameAndOrderId(username, orderId).orElseThrow(
				() -> new OrderException(OrderConstants.ORDER_ID_NOT_FOUND.formatted(orderId), HttpStatus.NOT_FOUND));
		Set<OrderItem> orderItems = orders.getOrderItems();
		orders.setOrderItems(orderItems);

		return List.of(orders);

	}

	@Transactional
	public Orders changeOrderStatus(String username, Integer orderId, String orderStatus) throws OrderException {
		Orders order = orderRepo.findByUsernameAndOrderId(username, orderId).orElseThrow(
				() -> new OrderException(OrderConstants.ORDER_ID_NOT_FOUND.formatted(orderId), HttpStatus.NOT_FOUND));

		if (order.getOrderStatus().equals(orderStatus)) {
			throw new OrderException(OrderConstants.ORDER_STATUS_ALREADY_CHANGED.formatted(orderId, orderStatus),
					HttpStatus.CONFLICT);
		}

		order.setOrderStatus(orderStatus);
		return order;
	}

}
