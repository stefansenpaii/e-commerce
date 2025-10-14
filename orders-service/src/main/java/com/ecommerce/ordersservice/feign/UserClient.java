package com.ecommerce.ordersservice.feign;

import com.ecommerce.ordersservice.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "USERS-SERVICE", fallback = UserFallback.class)
public interface UserClient {

    @GetMapping("/{userId}")
    UserDTO getUserById(@PathVariable("userId") Long userId);

}