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

	@Async("virtualThreadAsyncExecutor")
	public void decrementStockAsync(List<OrderItemDto> items) {
		log.info("Thread: {}", Thread.currentThread());
		log.info("decrementStock Async process started");
		decrementStock(items);
	}

	@Async("virtualThreadAsyncExecutor")
	public void restoreStockAsync(List<OrderItemDto> items) {
		log.info("restoreStock Async process started");
		restoreStock(items);
	}

	@Async("virtualThreadAsyncExecutor")
	public void sendEmailNotificationAsync(String username, String email, String msg) {
		log.info("sendEmailNotification Async process started");
		sendEmailNotification(username, email, msg);
	}

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

	public void sendEmailNotification(String username, String email, String msg) {
		try {
			if (email == null)
				email = authServerFeignClient.getEmail(username).getBody();
			log.info("extracted email: {}", email);

			// send email notification
			MailContentDto mailContentDto = new MailContentDto(email, null, msg, null);
			streamBridge.send("sendEmail-out-0", mailContentDto);
		} catch (FeignException e) {
			throw new DownStreamException(e.contentUTF8(), HttpStatus.valueOf(e.status()));
		} catch (MessageHandlingException e) {
			log.error(e.getMessage());
		}

	}

}
