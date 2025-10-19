package com.ecommerce.ordersservice.service;

import com.ecommerce.ordersservice.dto.OrderDetailsDTO;
import com.ecommerce.ordersservice.dto.UserDTO;
import com.ecommerce.ordersservice.feign.UserClient;
import com.ecommerce.ordersservice.models.Order;
import com.ecommerce.ordersservice.repository.OrderRepository;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.ws.rs.NotFoundException;
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
    public Optional<Order> save(Order order) {
        UserDTO userDTO;
        try {
            System.out.println("---Feign poziv ka USERS-SERVICE---");
            userDTO = userClient.getUserById(order.getUserId());
            if (userDTO == null) {
                System.err.println("Korisnik sa ID " + order.getUserId() + " nije pronađen.");
                return Optional.empty();
            }
        } catch (FeignException.NotFound e) {
            System.err.println("Korisnik sa ID " + order.getUserId() + " nije pronađen.");
            return Optional.empty();
        }
        catch (Exception e) {
            throw new RuntimeException("Validacija korisnika je nedostupna.", e);
        }

        if (userDTO == null) {
            System.err.println("Korisnik sa ID " + order.getUserId() + " nije pronađen.");
            return Optional.empty();
        }

        return Optional.of(orderRepository.save(order));
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

    public Optional<Order> saveFallback(Order order, Throwable t) {
        System.err.println("--- Fallback je aktiviran zbog: ---");
        System.err.println(t.getClass().getSimpleName() + ": " + t.getMessage());
        throw new RuntimeException("Greška: Validacija korisnika je nedostupna.");
    }

    public Optional<OrderDetailsDTO> getOrderDetailsFallback(Long orderId, Throwable t) {
        System.err.println("--- Fallback je aktiviran zbog: ---");
        System.err.println(t.getClass().getSimpleName() + ": " + t.getMessage());
        throw new RuntimeException("Greška: Validacija korisnika je nedostupna.");
    }

    @CircuitBreaker(name = "user-api", fallbackMethod = "getOrderDetailsFallback")
    @Retry(name = "user-api")
    public Optional<OrderDetailsDTO> getOrderDetails(Long orderId) {

        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isEmpty()) {
            return Optional.empty();
        }

        Order order = orderOptional.get();
        UserDTO userDTO = null;

        try {
            System.out.println("---Feign poziv ka USERS-SERVICE---");
            userDTO = userClient.getUserById(order.getUserId());
        } catch (FeignException.NotFound e) {
            System.err.println("Korisnik sa ID " + order.getUserId() + " nije pronađen.");
            return Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException("Validacija korisnika je nedostupna.", e);
        }
        if (userDTO == null) {
            return Optional.empty();
        }

        OrderDetailsDTO details = new OrderDetailsDTO();

        details.setOrderId(order.getId());
        details.setUserId(order.getUserId());
        details.setProductName(order.getProductName());
        details.setQuantity(order.getQuantity());

        details.setUserFirstName(userDTO.getFirstName());
        details.setUserLastName(userDTO.getLastName());
        details.setUserEmail(userDTO.getEmail());

        return Optional.of(details);
    }
}