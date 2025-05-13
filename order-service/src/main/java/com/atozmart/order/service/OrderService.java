package com.atozmart.order.service;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.atozmart.order.dao.OrdersDao;
import com.atozmart.order.dto.MailContentDto;
import com.atozmart.order.dto.PlaceOrderRequest;
import com.atozmart.order.dto.PlaceOrderResponce;
import com.atozmart.order.exception.OrderException;

import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class OrderService {

	private OrdersDao ordersDao;

	private StreamBridge streamBridge;

	private AuthServerFeignClient authServerFeignClient;

	@Transactional
	public PlaceOrderResponce placeOrder(String username, PlaceOrderRequest placeOrderRequest) throws OrderException {
		String orderId = ordersDao.placeOrder(username, placeOrderRequest);

		// send email notification
		String email = null;
		try {
			email = authServerFeignClient.getEmail(username).getBody();
		} catch (FeignException e) {
			throw new OrderException(e.getMessage(), HttpStatus.valueOf(e.status()));
		}

		MailContentDto mailContentDto = new MailContentDto(email, null, "order is placed with order id: " + orderId,
				null);
		streamBridge.send("orderConfirmationMail-out-0", mailContentDto);
		return new PlaceOrderResponce(orderId, "success");
	}

}
