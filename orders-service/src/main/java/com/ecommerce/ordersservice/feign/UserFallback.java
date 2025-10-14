package com.ecommerce.ordersservice.feign;

import com.ecommerce.ordersservice.dto.UserDTO;
import org.springframework.stereotype.Component;

@Component
public class UserFallback implements UserClient {

    @Override
    public UserDTO getUserById(Long userId) {
        System.out.println("--- Greška: Vracen Fallback za korisnika ID: " + userId + " ---");
        return null;
    }
}