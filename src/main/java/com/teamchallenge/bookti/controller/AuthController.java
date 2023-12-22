package com.teamchallenge.bookti.controller;

import com.teamchallenge.bookti.dto.ErrorResponse;
import com.teamchallenge.bookti.dto.authorization.NewUserRegistrationRequest;
import com.teamchallenge.bookti.dto.authorization.TokenPair;
import com.teamchallenge.bookti.dto.authorization.UserLoginRequest;
import com.teamchallenge.bookti.security.jwt.TokenGeneratorService;
import com.teamchallenge.bookti.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
@Tag(name = "Authorization mappings", description = "PERMIT_ALL")
@RequestMapping("/api/v1/authorize")
@RestController
public class AuthController {

    private final UserService userService;
    private final TokenGeneratorService tokenGeneratorService;
    private final AuthenticationManager authenticationManager;

    @Operation(
            summary = "User signup",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "User created successfully",
                            content = {
                                    @Content(
                                            mediaType = APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = TokenPair.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request or Validation failed",
                            content = {
                                    @Content(
                                            mediaType = APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = ErrorResponse.class))
                            }
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "User already exists",
                            content = {
                                    @Content(
                                            mediaType = APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = ErrorResponse.class))
                            }
                    )
            }
    )
    @PostMapping(path = "/signup")
    public ResponseEntity<TokenPair> signup(@Valid @RequestBody NewUserRegistrationRequest newUserRegistrationRequest) {
        var createdUser = userService.create(newUserRegistrationRequest);
        Authentication authentication = UsernamePasswordAuthenticationToken
                .authenticated(createdUser, createdUser.getPassword(), createdUser.getAuthorities());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(tokenGeneratorService.generateTokenPair(authentication));
    }

    @Operation(
            summary = "User login",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Login successful",
                            content = {
                                    @Content(
                                            mediaType = APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = TokenPair.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Invalid user credentials",
                            content = {
                                    @Content(
                                            mediaType = APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = ErrorResponse.class)
                                    )
                            }
                    )
            }
    )
    @PostMapping(path = "/login")
    public ResponseEntity<TokenPair> login(@Valid @RequestBody UserLoginRequest userCredentials) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userCredentials.getEmail(), userCredentials.getPassword())
        );
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(tokenGeneratorService.generateTokenPair(authentication));
    }
}

