package com.atozmart.cart.service;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
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
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class CartService {

	private CartDao cartDao;

	private CouponDao couponDao;

	private OrderFeignClient orderFeignClient;

	private ModelMapper mapper;

	public ViewCartResponse getCartDetails(String username) throws CartException {

		List<Cart> cartDetailLst = cartDao.getCartDetails(username);

		if (cartDetailLst.isEmpty())
			throw new CartException("cart is empty", HttpStatus.NOT_FOUND);

		ViewCartResponse response = new ViewCartResponse();
		List<ItemDto> items = new ArrayList<>();

		cartDetailLst.forEach(cartDetail -> {
			ItemDto item = new ItemDto();
			mapper.map(cartDetail, item);
			item.setEffectivePrice(cartDetail.getQuantity() * cartDetail.getUnitPrice());
			items.add(item);
		});

		response.setItems(items);
		Double orderAmount = items.stream().map(ItemDto::getEffectivePrice).reduce(Double::sum).orElse(0.0);
		response.setOrderAmount(orderAmount);

		return response;

	}

	public void addItemsToCart(ItemDto itemDto, String username) {
		cartDao.addItemsToCart(itemDto, username);
	}

	public void removeItemsFromCart(String username, String item, int quantity) throws CartException {
		cartDao.deleteItemsFromCart(username, item, quantity);

	}

	@Transactional
	public String proceedToPayment(String username, CheckOutRequest checkOutRequest) throws CartException {

		if (!checkOutRequest.paymentMode().equals("COD"))
			throw new CartException("can't place order for %s. only COD is eligible to place order"
					.formatted(checkOutRequest.paymentMode()), HttpStatus.BAD_REQUEST);

		// validate orderAmount
		ViewCartResponse cartDetails = getCartDetails(username);
		if (checkOutRequest.orderAmount() != cartDetails.getOrderAmount())
			throw new CartException("order amount mismatch", HttpStatus.BAD_REQUEST);

		// validate orderSavings
		double discountPercentage = couponDao.getCouponDiscount(checkOutRequest.couponCode());
		double discountAmount = checkOutRequest.orderAmount() * (discountPercentage / 100);
		if (discountAmount != checkOutRequest.orderSavings())
			throw new CartException("order savings mismatch", HttpStatus.BAD_REQUEST);

		// validate orderTotal
		if (checkOutRequest.orderTotal() != checkOutRequest.orderAmount() - discountAmount)
			throw new CartException("order total mismatch", HttpStatus.BAD_REQUEST);

		// payment

		// place order
		String orderId = placeOrder(username, checkOutRequest, cartDetails);

		// delete items from cart
		cartDao.deleteAllByusername(username);

		return orderId;
	}

	/**
	 * @returns orderId
	 */

	private String placeOrder(String username, CheckOutRequest checkOutRequest, ViewCartResponse cartDetails)
			throws CartException {

		PlaceOrderRequest placeOrderRequest = new PlaceOrderRequest();
		placeOrderRequest.setOrderAmount(checkOutRequest.orderAmount());
		placeOrderRequest.setCouponCode(checkOutRequest.couponCode());
		placeOrderRequest.setOrderSavings(checkOutRequest.orderSavings());
		placeOrderRequest.setOrderTotal(checkOutRequest.orderTotal());
		placeOrderRequest.setPaymentMode(checkOutRequest.paymentMode());

		List<OrderItemsDto> orderItems = cartDetails.getItems().stream()
				.map(item -> mapper.map(item, OrderItemsDto.class)).toList();

		placeOrderRequest.setItems(orderItems);

		log.debug("placeOrderRequest: {}", placeOrderRequest);

		try {
			ResponseEntity<PlaceOrderResponce> placeOrderResponse = orderFeignClient.placeOrder(username,
					placeOrderRequest);
			return placeOrderResponse.getBody().orderId();

		} catch (FeignException e) {
			throw new DownStreamException(e.contentUTF8(), HttpStatus.valueOf(e.status()));
		}
	}

}
