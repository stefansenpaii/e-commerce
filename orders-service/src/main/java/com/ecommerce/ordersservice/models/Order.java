package com.ecommerce.ordersservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "app_order")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "ID korisnika je obavezan")
    private Long userId;

    @NotBlank(message = "Ime proizvoda je obavezno")
    @Size(min = 2, max = 100, message = "Ime produkta mora imati 2-100 karaktera")
    private String productName;

    @NotNull(message = "Količina je obavezna")
    @Min(value = 1, message = "Količina mora biti bar 1")
    private Integer quantity;
}