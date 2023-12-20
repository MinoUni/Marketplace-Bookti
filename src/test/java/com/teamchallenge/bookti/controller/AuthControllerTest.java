package com.teamchallenge.bookti.controller;

import com.teamchallenge.bookti.dto.authorization.NewUserRegistrationRequest;
import io.swagger.v3.core.util.Json;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AuthenticationManager authenticationManager;

    @Test
    @DisplayName("When calling /signup endpoint, expect to create a new User and generate access & refresh token pair with CREATED(201) status")
    void shouldReturnTokenPairDTOWithStatusCode201() throws Exception {
        NewUserRegistrationRequest userDetails = new NewUserRegistrationRequest(
                "FirstName",
                "LastName",
                "abc@gmail.com",
                "Password1",
                "Password1"
        );
        when(authenticationManager.authenticate(any())).thenReturn(any(Authentication.class));
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/authorize/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Json.pretty(userDetails)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("user_id").exists())
                .andExpect(jsonPath("access_token").exists())
                .andExpect(jsonPath("refresh_token").exists());
    }
}