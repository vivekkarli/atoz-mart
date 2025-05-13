package com.atozmart.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.atozmart.order.entity.Orders;

public interface OrdersRepository extends JpaRepository<Orders, Integer>{

}
