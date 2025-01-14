package com.chumore.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.chumore.auth.dto.LoginRequest;
import com.chumore.auth.dto.RegisterRequest;
import com.chumore.auth.dto.RestaurantRegisterRequest;
import com.chumore.auth.service.AuthService;
import com.chumore.auth.exception.AuthenticationException;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private AuthService authService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    public void whenLogin_withValidCredentials_thenRedirectToDashboard() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        when(authService.authenticate(any(LoginRequest.class)))
                .thenReturn(1);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email", loginRequest.getEmail())
                .param("password", loginRequest.getPassword()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));
    }

    @Test
    public void whenLogin_withInvalidCredentials_thenRedirectToLogin() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("wrongpassword");

        when(authService.authenticate(any(LoginRequest.class)))
                .thenThrow(new AuthenticationException("帳號或密碼錯誤"));

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email", loginRequest.getEmail())
                .param("password", loginRequest.getPassword()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }

    @Test
    public void whenRegisterMember_withValidData_thenRedirectToLogin() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("new@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setName("Test User");
        registerRequest.setPhone("0912345678");
        registerRequest.setGender(1);
        
        // 正確使用 java.sql.Date
        java.sql.Date birthdate = java.sql.Date.valueOf("2000-01-01");
        registerRequest.setBirthdate(birthdate);

        when(authService.registerMember(any(RegisterRequest.class)))
            .thenReturn(1);

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email", registerRequest.getEmail())
                .param("password", registerRequest.getPassword())
                .param("name", registerRequest.getName())
                .param("phone", registerRequest.getPhone())
                .param("gender", "1")
                .param("birthdate", "2000-01-01"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }
    
    @Test
    public void whenRegisterRestaurant_withValidData_thenRedirectToLogin() throws Exception {
        RestaurantRegisterRequest request = new RestaurantRegisterRequest();
        request.setEmail("restaurant@example.com");
        request.setPassword("password123");
        request.setName("Restaurant Owner");
        request.setPhone("0912345678");
        request.setRestaurantName("Test Restaurant");
        request.setBusinessRegistrationNumber("12345678");
        request.setBusinessPhone("0223456789");

        when(authService.registerRestaurant(any(RestaurantRegisterRequest.class)))
            .thenReturn(1);

        mockMvc.perform(post("/auth/register/restaurant")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email", request.getEmail())
                .param("password", request.getPassword())
                .param("name", request.getName())
                .param("phone", request.getPhone())
                .param("restaurantName", request.getRestaurantName())
                .param("businessRegistrationNumber", request.getBusinessRegistrationNumber())
                .param("businessPhone", request.getBusinessPhone()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }
}