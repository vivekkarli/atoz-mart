package com.atozmart.cart.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.atozmart.cart.dao.CartDao;
import com.atozmart.cart.dao.CouponDao;
import com.atozmart.cart.dto.CheckOutRequest;
import com.atozmart.cart.dto.ItemDto;
import com.atozmart.cart.dto.ViewCartResponse;
import com.atozmart.cart.dto.order.OrderItemsDto;
import com.atozmart.cart.dto.order.PlaceOrderRequest;
import com.atozmart.cart.dto.order.PlaceOrderResponce;
import com.atozmart.cart.entity.Cart;
import com.atozmart.cart.exception.CartException;
import com.atozmart.commons.exception.dto.DownStreamException;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {

	private final CartDao cartDao;

	private final CouponDao couponDao;

	private final OrderFeignClient orderFeignClient;

	public ViewCartResponse getCartDetails(String username) throws CartException {

		List<Cart> cartDetailLst = cartDao.getCartDetails(username);

		if (cartDetailLst.isEmpty())
			throw new CartException("cart is empty", HttpStatus.NOT_FOUND);

		List<ItemDto> items = new ArrayList<>();

		cartDetailLst.forEach(cartDetail -> {
			BigDecimal effectivePrice = cartDetail.getEffectivePrice();
			BigDecimal roundedEffectivePrice = effectivePrice.setScale(2, RoundingMode.DOWN);

			ItemDto item = new ItemDto(cartDetail.getItemId(), cartDetail.getItemName(), cartDetail.getUnitPrice(),
					cartDetail.getQuantity(), roundedEffectivePrice);
			items.add(item);
		});

		BigDecimal orderAmount = items.stream().map(ItemDto::effectivePrice).reduce(BigDecimal.ZERO, BigDecimal::add);
		BigDecimal roundedOrderAmt = orderAmount.setScale(2, RoundingMode.DOWN);
		log.info("orderAmount: {}, roundedOrderAmt: {}", orderAmount, roundedOrderAmt);

		return new ViewCartResponse(items, roundedOrderAmt);

	}

	public void addOrUpdateItemInCart(ItemDto itemDto, String username) {
		cartDao.addOrUpdateItemInCart(itemDto, username);
	}

	public void updateItemQuantity(ItemDto itemDto, String username) {
		cartDao.updateItemQuantity(itemDto.quantity(), itemDto.itemId(), username);
	}

	public void removeItemsFromCart(String username, String itemId) throws CartException {
		cartDao.deleteItems(username, itemId);
	}

	public String proceedToPayment(String username, String email, CheckOutRequest checkOutRequest)
			throws CartException {
		ViewCartResponse cartDetails = getCartDetails(username);
		validateCheckoutRequest(checkOutRequest, cartDetails);

		// payment // TODO

		String orderId = placeOrder(username, email, checkOutRequest, cartDetails);
		cartDao.deleteItems(username, null);
		return orderId;
	}

	private void validateCheckoutRequest(CheckOutRequest checkOutRequest, ViewCartResponse cartDetails) {
		if (!checkOutRequest.paymentMode().equals("COD"))
			throw new CartException("can't place order for %s. only COD is eligible to place order"
					.formatted(checkOutRequest.paymentMode()), HttpStatus.BAD_REQUEST);

		// validate orderAmount
		if (cartDetails.orderAmount().compareTo(checkOutRequest.orderAmount()) != 0) {
			throw new CartException("order amount mismatch", HttpStatus.BAD_REQUEST);
		}

		// validate orderSavings
		double discountPercentage = checkOutRequest.couponCode() == null || checkOutRequest.couponCode().isBlank() ? 0
				: couponDao.getCouponDiscount(checkOutRequest.couponCode());
		var discountAmount = checkOutRequest.orderAmount().multiply(BigDecimal.valueOf(discountPercentage / 100))
				.setScale(2, RoundingMode.HALF_EVEN);
		if (discountAmount.compareTo(checkOutRequest.orderSavings()) != 0)
			throw new CartException("order savings mismatch", HttpStatus.BAD_REQUEST);

		// validate orderTotal
		BigDecimal expectedOrderTotal = checkOutRequest.orderAmount().subtract(discountAmount);
		if (expectedOrderTotal.compareTo(checkOutRequest.orderTotal()) != 0)
			throw new CartException("order total mismatch", HttpStatus.BAD_REQUEST);
	}

	/**
	 * @return orderId
	 */
	private String placeOrder(String username, String email, CheckOutRequest checkOutRequest,
			ViewCartResponse cartDetails) throws CartException {

		List<OrderItemsDto> orderItems = new ArrayList<>();
		cartDetails.items().forEach(item -> orderItems.add(new OrderItemsDto(item.itemId(), item.itemName(),
				item.unitPrice(), item.quantity(), item.effectivePrice())));

		PlaceOrderRequest placeOrderRequest = new PlaceOrderRequest(checkOutRequest.orderAmount(),
				checkOutRequest.couponCode(), checkOutRequest.orderSavings(), checkOutRequest.orderTotal(),
				checkOutRequest.paymentMode(), orderItems);

		log.debug("placeOrderRequest: {}", placeOrderRequest);

		try {
			ResponseEntity<PlaceOrderResponce> placeOrderResponse = orderFeignClient.placeOrder(username, email,
					placeOrderRequest);
			return placeOrderResponse.getBody().orderId();

		} catch (FeignException e) {
			throw new DownStreamException(e.contentUTF8(), HttpStatus.valueOf(e.status()));
		}

	}

}
