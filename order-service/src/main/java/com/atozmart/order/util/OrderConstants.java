package com.atozmart.order.util;

public class OrderConstants {

	private OrderConstants() {

	}

	public static final String ORDER_CANCEL_MAIL_CONTENT = """
			your order with order id: %s is cancelled successfully,
			orderTotal: %s will be refunded in 3 business days""";

	public static final String ORDER_PLACED_MAIL_CONTENT = "order is placed with order id: %s";
	public static final String ORDER_STATUS_ALREADY_CHANGED = "order with order id: %d is already in \"%s\" status";
	public static final String ORDER_ID_NOT_FOUND = "order with order id: %d not found";
	public static final String ORDER_NOT_FOUND = "no orders found";

	public static String getOrderCancelMailContent(Integer orderId, Double orderTotal) {
		return ORDER_CANCEL_MAIL_CONTENT.formatted(orderId, orderTotal);
	}

	public static String getOrderPlacedMailContent(String orderId) {
		return ORDER_PLACED_MAIL_CONTENT.formatted(orderId);
	}

}
