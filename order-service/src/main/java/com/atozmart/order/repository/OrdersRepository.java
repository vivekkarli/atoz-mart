package com.atozmart.order.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.atozmart.order.entity.Orders;

public interface OrdersRepository extends JpaRepository<Orders, Integer> {

	Optional<Orders> findByUsernameAndOrderId(String username, Integer orderId);

	List<Orders> findByUsername(String username);
}
