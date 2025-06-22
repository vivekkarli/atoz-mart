package com.atozmart.order.util;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.atozmart.order.dto.OrderItemDto;
import com.atozmart.order.entity.OrderItem;

@Mapper
public interface OrdersMapper {
	
	OrdersMapper INSTANCE = Mappers.getMapper(OrdersMapper.class);
	
	List<OrderItemDto> orderItemsToOrderItemsDto(List<OrderItem> orderItems);
	
}
