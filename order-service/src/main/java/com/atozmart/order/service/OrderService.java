package com.atozmart.order.service;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.atozmart.commons.exception.dto.DownStreamException;
import com.atozmart.order.dao.OrdersDao;
import com.atozmart.order.dto.MailContentDto;
import com.atozmart.order.dto.PlaceOrderRequest;
import com.atozmart.order.dto.PlaceOrderResponce;
import com.atozmart.order.dto.ViewOrdersDto;
import com.atozmart.order.entity.Orders;
import com.atozmart.order.exception.OrderException;

import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

	private final OrdersDao ordersDao;

	private final StreamBridge streamBridge;

	private final ModelMapper mapper;

	private final AuthServerFeignClient authServerFeignClient;

	@Transactional
	public PlaceOrderResponce placeOrder(String username, String email, PlaceOrderRequest placeOrderRequest)
			throws OrderException {
		String orderId = ordersDao.placeOrder(username, placeOrderRequest);
		sendEmailNotification(username, email, orderId);
		return new PlaceOrderResponce(orderId, "success");
	}

	private void sendEmailNotification(String username, String email, String orderId) {
		try {
			if (email == null)
				email = authServerFeignClient.getEmail(username).getBody();
		} catch (FeignException e) {
			throw new DownStreamException(e.contentUTF8(), HttpStatus.valueOf(e.status()));
		}

		// send email notification
		MailContentDto mailContentDto = new MailContentDto(email, null, "order is placed with order id: " + orderId,
				null);
		streamBridge.send("sendEmail-out-0", mailContentDto);
	}

	public List<ViewOrdersDto> getOrderDetails(String username, Integer orderId) {

		List<Orders> orderDetails = ordersDao.getOrderDetails(username, orderId);
		log.info("orderDetails: {}", orderDetails);

		List<ViewOrdersDto> viewOrdersDtos = new ArrayList<>();

		orderDetails.forEach(order -> {
			if (orderId == null) {
				ViewOrdersDto viewOrdersDto = new ViewOrdersDto();
				viewOrdersDto.setOrderId(order.getOrderId());
				viewOrdersDto.setDeliveryStatus(order.getDeliveryStatus());
				viewOrdersDto.setPaymentStatus(order.getPaymentStatus());
				viewOrdersDto.setOrderTotal(order.getOrderTotal());

				viewOrdersDtos.add(viewOrdersDto);
				return;
			}

			ViewOrdersDto viewOrdersDto = mapper.map(order, ViewOrdersDto.class);
			viewOrdersDtos.add(viewOrdersDto);
		});

		return viewOrdersDtos;

	}

}
