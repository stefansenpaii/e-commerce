package com.ecommerce.ordersservice.controller;

import com.ecommerce.ordersservice.dto.OrderDetailsDTO;
import com.ecommerce.ordersservice.models.Order;
import com.ecommerce.ordersservice.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return orderService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Order createOrder(@Valid @RequestBody Order order) {
        return orderService.save(order);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @Valid @RequestBody Order orderDetails) {
        return orderService.findById(id)
                .map(existingOrder -> {
                    existingOrder.setUserId(orderDetails.getUserId()!=null ? orderDetails.getUserId() : existingOrder.getUserId());
                    existingOrder.setQuantity(orderDetails.getQuantity()!=null ? orderDetails.getQuantity() : existingOrder.getQuantity());
                    existingOrder.setProductName(orderDetails.getProductName()!=null ? orderDetails.getProductName() : existingOrder.getProductName());
                    return ResponseEntity.ok(orderService.save(existingOrder));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        if (orderService.findById(id).isPresent()) {
            orderService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{orderId}/details")
    public OrderDetailsDTO getOrderDetails(@PathVariable Long orderId) {
        return orderService.getOrderDetails(orderId);
    }
}
