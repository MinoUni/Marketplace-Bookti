package com.teamchallenge.bookti.controller;

import com.teamchallenge.bookti.dto.ErrorResponse;
import com.teamchallenge.bookti.dto.user.UserInfo;
import com.teamchallenge.bookti.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Find User information",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User information found",
                            content = {
                                    @Content(
                                            mediaType = APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = UserInfo.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "UNAUTHORIZED",
                            content = {
                                    @Content(
                                            mediaType = APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = ErrorResponse.class))
                            }
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "ACCESS_DENIED",
                            content = {
                                    @Content(
                                            mediaType = APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = ErrorResponse.class))
                            }
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "NOT_FOUND",
                            content = {
                                    @Content(
                                            mediaType = APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = ErrorResponse.class))
                            }
                    )
            }
    )
    @GetMapping(path = "/{id}")
    @PreAuthorize("isAuthenticated() and authentication.principal.id == #id")
    public ResponseEntity<UserInfo> getUserInfo(@PathVariable UUID id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.findById(id));
    }
}
