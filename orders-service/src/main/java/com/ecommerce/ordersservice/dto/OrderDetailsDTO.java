package com.ecommerce.ordersservice.dto;

import lombok.Data;

@Data
public class OrderDetailsDTO {

    private Long orderId;
    private Long userId;
    private String productName;
    private Integer quantity;
    private Double totalAmount;

    private String userFirstName;
    private String userLastName;
    private String userEmail;

}