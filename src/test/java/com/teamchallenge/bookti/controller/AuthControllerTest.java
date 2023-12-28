package com.teamchallenge.bookti.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamchallenge.bookti.Application;
import com.teamchallenge.bookti.dto.authorization.*;
import com.teamchallenge.bookti.exception.PasswordIsNotMatchesException;
import com.teamchallenge.bookti.exception.UserAlreadyExistsException;
import com.teamchallenge.bookti.exception.UserNotFoundException;
import com.teamchallenge.bookti.security.AuthorizedUser;
import com.teamchallenge.bookti.security.jwt.TokenGeneratorService;
import com.teamchallenge.bookti.service.impl.DefaultUserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = Application.class
)
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper jsonMapper;
    @MockBean
    private DefaultUserService userService;
    @MockBean
    private TokenGeneratorService tokenService;

    @Test
    @DisplayName("When calling /signup, expect that new User with access & refresh token pair will be created with status code 201")
    void shouldReturnTokenPairDTOWithStatusCode201() throws Exception {
        NewUserRegistrationRequest userDetails = new NewUserRegistrationRequest(
                "fullName",
                "abc@gmail.com",
                "Password1",
                "Password1"
        );
        var user = AuthorizedUser
                .authorizedUserBuilder(userDetails.getEmail(), userDetails.getPassword(), List.of())
                .id(UUID.randomUUID())
                .build();

        when(userService.create(userDetails)).thenReturn(user);
        when(tokenService.generateTokenPair(any(Authentication.class)))
                .thenReturn(TokenPair.builder()
                        .userId(user.getId().toString())
                        .accessToken("access_token")
                        .refreshToken("refresh_token")
                        .build());

        mockMvc.perform(post("/api/v1/authorize/signup")
                        .contentType(APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(userDetails)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("user_id").value(user.getId().toString()))
                .andExpect(jsonPath("access_token").exists())
                .andExpect(jsonPath("refresh_token").exists());

        verify(userService, times(1)).create(userDetails);
    }

    @Test
    @DisplayName("When calling /signup, expect that passwords in req not matches with status code 400")
    void shouldReturnErrorResponseWithPasswordIsNotMatchesExceptionWithStatusCode400() throws Exception {
        NewUserRegistrationRequest userDetails = new NewUserRegistrationRequest(
                "FirstName",
                "abc@gmail.com",
                "Password1",
                "PassNoWord2"
        );
        when(userService.create(userDetails))
                .thenThrow(new PasswordIsNotMatchesException("Password is not matches"));

        mockMvc.perform(post("/api/v1/authorize/signup")
                        .contentType(APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(userDetails)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("timestamp").exists())
                .andExpect(jsonPath("status_code").value("400"))
                .andExpect(jsonPath("message").value("Password is not matches"));

        verify(userService, times(1)).create(userDetails);
    }

    @Test
    @DisplayName("When calling /signup with invalid request body, expect validation failed with status code 400")
    void shouldReturnErrorResponseValidationExceptionWithStatusCode400() throws Exception {
        NewUserRegistrationRequest userDetails = new NewUserRegistrationRequest(
                "",
                "abcgmail.com",
                "Password1",
                "PassNoWord2"
        );
        mockMvc.perform(post("/api/v1/authorize/signup")
                        .contentType(APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(userDetails)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("timestamp").exists())
                .andExpect(jsonPath("status_code").value("400"))
                .andExpect(jsonPath("message").value("Validation failed"));

        verify(userService, never()).create(userDetails);
    }

    @Test
    @DisplayName("when calling /signup, expect that user already exists and status code 409 will be thrown")
    void shouldReturnErrorResponseWithUserAlreadyExistsExceptionWithStatusCode409() throws Exception {
        NewUserRegistrationRequest userDetails = new NewUserRegistrationRequest(
                "first_name",
                "abc@gmail.com",
                "Password1",
                "Password1"
        );

        when(userService.create(userDetails)).thenThrow(UserAlreadyExistsException.class);

        mockMvc.perform(post("/api/v1/authorize/signup")
                        .contentType(APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(userDetails)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("timestamp").exists())
                .andExpect(jsonPath("status_code").value("409"));

        verify(userService, times(1)).create(userDetails);
    }

    @Test
    @DisplayName("when calling /login/resetPassword, expect that user with email from request already exists and mail will be sent with status code 201")
    void shouldReturnPasswordResetResponseWithStatusCode201() throws Exception {
         //TODO test
    }

    @Test
    @DisplayName("when calling /login/resetPassword, expect that user with email from request does not exists and status code 404 will be thrown")
    void shouldReturnErrorResponseWithUserNotFoundExceptionWithStatusCode404() throws Exception {
        MailResetPasswordRequest mailResetPasswordRequest = new MailResetPasswordRequest("a@gmail.com");
        when(userService.findUserByEmail(mailResetPasswordRequest.getEmail())).thenThrow(UserNotFoundException.class);

        mockMvc.perform(post("/api/v1/authorize/login/resetPassword")
                        .contentType(APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(mailResetPasswordRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("timestamp").exists())
                .andExpect(jsonPath("status_code").value("404"));
    }

    @Test
    @DisplayName("when calling /login/resetPassword with invalid request body, expect validation failed with status code 400")
    void shouldReturnErrorResponseWithValidationExceptionWithStatusCode400() throws Exception {
        MailResetPasswordRequest mailResetPasswordRequest = new MailResetPasswordRequest("abc123");
        mockMvc.perform(post("/api/v1/authorize/login/resetPassword")
                        .contentType(APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(mailResetPasswordRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("timestamp").exists())
                .andExpect(jsonPath("status_code").value("400"))
                .andExpect(jsonPath("message").value("Validation failed"));

        verify(userService, never()).findUserByEmail(mailResetPasswordRequest.getEmail());
    }
}
