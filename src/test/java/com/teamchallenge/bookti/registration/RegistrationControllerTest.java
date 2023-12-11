package com.teamchallenge.bookti.registration;

import com.teamchallenge.bookti.controller.RegistrationController;
import com.teamchallenge.bookti.dto.registration.NewUserRegistrationRequest;
import com.teamchallenge.bookti.service.impl.DefaultUserService;
import io.swagger.v3.core.util.Json;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(RegistrationController.class)
class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DefaultUserService defaultUserService;

    @Test
    void whenRegistrationSuccessfulThenDisplayRegistrationResponseWithStatusCreated() throws Exception {
        NewUserRegistrationRequest newUserRegistrationRequest = NewUserRegistrationRequest
                .builder()
                .firstName("FirstName")
                .lastName("LastName")
                .email("abc@gmail.com")
                .password("Password1")
                .confirmPassword("Password1")
                .build();

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/registration/register")
                        .content(Json.pretty(newUserRegistrationRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }
}