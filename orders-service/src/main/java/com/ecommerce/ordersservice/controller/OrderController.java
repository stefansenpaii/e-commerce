package com.ecommerce.ordersservice.controller;

import com.ecommerce.ordersservice.dto.OrderDetailsDTO;
import com.ecommerce.ordersservice.models.Order;
import com.ecommerce.ordersservice.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.findAll();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return orderService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody Order order) {

        return orderService.save(order)
                .map(savedOrder -> {
                    URI location = ServletUriComponentsBuilder
                            .fromCurrentRequest()
                            .path("/{id}")
                            .buildAndExpand(savedOrder.getId())
                            .toUri();

                    return ResponseEntity.created(location).body(savedOrder);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @RequestBody Order orderDetails) {
        return orderService.findById(id)
                .map(existingOrder -> {
                    existingOrder.setUserId(orderDetails.getUserId() != null ? orderDetails.getUserId() : existingOrder.getUserId());
                    existingOrder.setQuantity(orderDetails.getQuantity() != null ? orderDetails.getQuantity() : existingOrder.getQuantity());
                    existingOrder.setProductName(orderDetails.getProductName() != null ? orderDetails.getProductName() : existingOrder.getProductName());
                    return existingOrder;
                })
                .flatMap(orderToSave -> orderService.save(orderToSave))
                .map(ResponseEntity::ok)
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
    public ResponseEntity<OrderDetailsDTO> getOrderDetails(@PathVariable Long orderId) {
        return orderService.getOrderDetails(orderId).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
