package com.atozmart.order.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.MessageHandlingException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.atozmart.commons.exception.dto.DownStreamException;
import com.atozmart.order.dto.MailContentDto;
import com.atozmart.order.dto.OrderItemDto;
import com.atozmart.order.dto.catalog.SingleStockUpdateDto;
import com.atozmart.order.dto.catalog.StockUpdateDto;

import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class OrderServiceHelper {

	private final StreamBridge streamBridge;

	private final AuthServerFeignClient authServerFeignClient;

	private final CatalogFeignClient catalogFeignClient;

	public void decrementStock(List<OrderItemDto> items) {

		List<SingleStockUpdateDto> singleStockUpdates = new ArrayList<>();
		items.forEach(item -> singleStockUpdates.add(new SingleStockUpdateDto(item.itemId(), null, item.quantity())));
		StockUpdateDto stockUpdateDto = new StockUpdateDto(singleStockUpdates);

		try {
			catalogFeignClient.updateStock(stockUpdateDto);
		} catch (FeignException e) {
			throw new DownStreamException(e.contentUTF8(), HttpStatus.valueOf(e.status()));
		}
	}

	public void restoreStock(List<OrderItemDto> items) {

		List<SingleStockUpdateDto> singleStockUpdates = new ArrayList<>();
		items.forEach(item -> singleStockUpdates.add(new SingleStockUpdateDto(item.itemId(), item.quantity(), null)));
		StockUpdateDto stockUpdateDto = new StockUpdateDto(singleStockUpdates);

		try {
			catalogFeignClient.updateStock(stockUpdateDto);
		} catch (FeignException e) {
			throw new DownStreamException(e.contentUTF8(), HttpStatus.valueOf(e.status()));
		}
	}

	@Async
	public void restoreStockAsync(List<OrderItemDto> items) {
		log.info("restore Stock Async process started");
		restoreStock(items);
	}

	public void sendEmailNotification(String username, String email, String orderId) {
		try {
			if (email == null)
				email = authServerFeignClient.getEmail(username).getBody();
			log.info("extracted email: {}", email);

			// send email notification
			MailContentDto mailContentDto = new MailContentDto(email, null, "order is placed with order id: " + orderId,
					null);
			streamBridge.send("sendEmail-out-0", mailContentDto);
		} catch (FeignException e) {
			throw new DownStreamException(e.contentUTF8(), HttpStatus.valueOf(e.status()));
		} catch (MessageHandlingException e) {
			log.error(e.getMessage());
		}

	}

}
