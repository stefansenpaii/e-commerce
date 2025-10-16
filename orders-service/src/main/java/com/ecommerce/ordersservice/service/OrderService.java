package com.ecommerce.ordersservice.service;

import com.ecommerce.ordersservice.dto.OrderDetailsDTO;
import com.ecommerce.ordersservice.dto.UserDTO;
import com.ecommerce.ordersservice.feign.UserClient;
import com.ecommerce.ordersservice.models.Order;
import com.ecommerce.ordersservice.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserClient userClient;

    @CircuitBreaker(name = "user-api", fallbackMethod = "saveFallback")
    @Retry(name = "user-api")
    public Order save(Order order) {
        try {
            userClient.getUserById(order.getUserId());
        } catch (Exception e) {
            throw new RuntimeException("Korisnik sa ID " + order.getUserId() + " ne postoji.", e);
        }
        return orderRepository.save(order);
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    public void deleteById(Long id) {
        orderRepository.deleteById(id);
    }

    public Order saveFallback(Order order, Throwable t) {
        System.err.println("--- Fallback je aktiviran zbog: " + t.getMessage() + " ---");
        throw new RuntimeException("Kvar usluge: Validacija korisnika je nedostupna.");
    }

    public OrderDetailsDTO getOrderDetails(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Narudžbina nije pronađena."));

        UserDTO userDTO = userClient.getUserById(order.getUserId());

        if (userDTO == null) {
            throw new RuntimeException("Podaci o korisniku trenutno nedostupni.");
        }

        OrderDetailsDTO details = new OrderDetailsDTO();

        details.setOrderId(order.getId());
        details.setUserId(order.getUserId());
        details.setProductName(order.getProductName());
        details.setQuantity(order.getQuantity());

        details.setUserFirstName(userDTO.getFirstName());
        details.setUserLastName(userDTO.getLastName());
        details.setUserEmail(userDTO.getEmail());

        return details;
    }
}