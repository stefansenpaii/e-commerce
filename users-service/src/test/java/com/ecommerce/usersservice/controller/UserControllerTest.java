package com.ecommerce.usersservice.controller;

import com.ecommerce.usersservice.models.User;
import com.ecommerce.usersservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = UserController.class,
        excludeAutoConfiguration = {
                JpaRepositoriesAutoConfiguration.class,
                HibernateJpaAutoConfiguration.class
        },
        properties = "spring.cloud.discovery.enabled=false"
)
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void shouldReturnUserWhenExists() throws Exception {

        Long userId = 1L;
        User user = new User(userId, "Pera", "Peric", "pera@gmail.com");

        when(userService.findById(userId)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.firstName").value("Pera"))
                .andExpect(jsonPath("$.lastName").value("Peric"))
                .andExpect(jsonPath("$.email").value("pera@gmail.com"));
    }

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        Long nonExistentId = 99L;
        when(userService.findById(nonExistentId)).thenReturn(Optional.empty());
        mockMvc.perform(get("/users/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }
}