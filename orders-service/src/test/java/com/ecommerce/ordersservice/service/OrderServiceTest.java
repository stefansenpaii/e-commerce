package com.ecommerce.ordersservice.service;

import com.ecommerce.ordersservice.feign.UserClient;
import com.ecommerce.ordersservice.models.Order;
import com.ecommerce.ordersservice.repository.OrderRepository;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserClient userServiceClient;

    @InjectMocks
    private OrderService orderService;

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        Order newOrder = new Order(null, 99L, "Laptop", 1);

        when(userServiceClient.getUserById(99L)).thenThrow(new NotFoundException("User not found"));

        Assertions.assertThrows(
                RuntimeException.class,
                () -> orderService.save(newOrder),
                "Očekivani RuntimeException nije bačen."
        );

        verify(orderRepository, never()).save(any(Order.class));
    }
}