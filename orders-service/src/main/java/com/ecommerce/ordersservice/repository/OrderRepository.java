package com.ecommerce.ordersservice.repository;

import com.ecommerce.ordersservice.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}