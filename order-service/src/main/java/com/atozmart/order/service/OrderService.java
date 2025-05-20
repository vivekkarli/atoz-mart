package com.atozmart.order.service;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.atozmart.commons.exception.dto.DownStreamException;
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
	public PlaceOrderResponce placeOrder(String username, String email, PlaceOrderRequest placeOrderRequest) throws OrderException {
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

}
