package com.ecommerce.usersservice.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "app_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Ime je obavezno")
    @Size(min = 2, max = 100, message = "Ime mora imati 2-100 karaktera")
    private String firstName;

    @NotBlank(message = "Prezime je obavezno")
    @Size(min = 2, max = 100, message = "Prezime mora imati 2-100 karaktera")
    private String lastName;

    @NotBlank(message = "Email je obavezan")
    @Size(min = 2, max = 100, message = "Email mora imati 2-100 karaktera")
    private String email;

}