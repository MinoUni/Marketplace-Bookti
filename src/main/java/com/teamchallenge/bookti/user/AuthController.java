package com.teamchallenge.bookti.user;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.security.authentication.UsernamePasswordAuthenticationToken.authenticated;

import com.teamchallenge.bookti.dto.AppResponse;
import com.teamchallenge.bookti.dto.ErrorResponse;
import com.teamchallenge.bookti.exception.user.RefreshTokenAlreadyRevokedException;
import com.teamchallenge.bookti.security.jwt.TokenManager;
import com.teamchallenge.bookti.user.dto.TokenPair;
import com.teamchallenge.bookti.user.dto.UserLoginDto;
import com.teamchallenge.bookti.user.dto.UserSaveDto;
import com.teamchallenge.bookti.user.dto.UserTokenPair;
import com.teamchallenge.bookti.user.password.MailResetPasswordRequest;
import com.teamchallenge.bookti.user.password.MailResetPasswordResponse;
import com.teamchallenge.bookti.user.password.PasswordResetRequest;
import com.teamchallenge.bookti.utils.EmailUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authorize")
class AuthController {

  private final UserService userService;
  private final TokenManager tokenManager;
  private final AuthenticationManager authenticationManager;
  private final JwtAuthenticationProvider refreshTokenProvider;
  private final EmailUtils emailUtils;

  public AuthController(
      UserService userService,
      TokenManager tokenManager,
      AuthenticationManager authenticationManager,
      EmailUtils emailUtils,
      @Qualifier("jwtRefreshTokenAuthenticationProvider")
          JwtAuthenticationProvider refreshTokenProvider) {
    this.userService = userService;
    this.tokenManager = tokenManager;
    this.authenticationManager = authenticationManager;
    this.emailUtils = emailUtils;
    this.refreshTokenProvider = refreshTokenProvider;
  }

  @Operation(
      summary = "User signup",
      responses = {
        @ApiResponse(
            responseCode = "201",
            description = "User created successfully",
            content = {
              @Content(
                  mediaType = APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = TokenPair.class))
            }),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request or Validation failed",
            content = {
              @Content(
                  mediaType = APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorResponse.class))
            }),
        @ApiResponse(
            responseCode = "409",
            description = "User already exists",
            content = {
              @Content(
                  mediaType = APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorResponse.class))
            }),
        @ApiResponse(
            responseCode = "500",
            description = "INTERNAL_SERVER_ERROR",
            content = {
              @Content(
                  mediaType = APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorResponse.class))
            })
      })
  @PostMapping(
      path = "/signup",
      consumes = APPLICATION_JSON_VALUE,
      produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<TokenPair> signup(@Valid @RequestBody UserSaveDto newUser) {
    var createdUser = userService.create(newUser);
    Authentication authentication =
        authenticated(createdUser, createdUser.getPassword(), createdUser.getAuthorities());
    return ResponseEntity.status(HttpStatus.CREATED)
        .contentType(APPLICATION_JSON)
        .body(tokenManager.generateTokenPair(authentication));
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
                  schema = @Schema(implementation = TokenPair.class))
            }),
        @ApiResponse(
            responseCode = "401",
            description = "Invalid user credentials",
            content = {
              @Content(
                  mediaType = APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorResponse.class))
            }),
        @ApiResponse(
            responseCode = "500",
            description = "INTERNAL_SERVER_ERROR",
            content = {
              @Content(
                  mediaType = APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorResponse.class))
            })
      })
  @PostMapping(path = "/login")
  public ResponseEntity<TokenPair> login(@Valid @RequestBody UserLoginDto credentials)
      throws BadCredentialsException {
    var authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                credentials.getEmail(), credentials.getPassword()));
    return ResponseEntity.status(HttpStatus.OK)
        .contentType(APPLICATION_JSON)
        .body(tokenManager.generateTokenPair(authentication));
  }

  @Operation(
      summary = "Get new access JWT",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "New access and refresh JWT pair generated",
            content = {
              @Content(
                  mediaType = APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = TokenPair.class))
            }),
        @ApiResponse(
            responseCode = "401",
            description = "Provided refresh JWT is <Invalid> or <Expired>",
            content = {
              @Content(
                  mediaType = APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorResponse.class))
            }),
        @ApiResponse(
            responseCode = "409",
            description = "Provided refresh JWT is <Revoked>",
            content = {
              @Content(
                  mediaType = APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorResponse.class))
            }),
        @ApiResponse(
            responseCode = "500",
            description = "INTERNAL_SERVER_ERROR",
            content = {
              @Content(
                  mediaType = APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorResponse.class))
            })
      })
  @PostMapping("/token/refresh")
  public ResponseEntity<TokenPair> refreshToken(@RequestBody UserTokenPair refreshToken)
      throws BadCredentialsException, RefreshTokenAlreadyRevokedException {
    String token = refreshToken.getRefreshToken();
    if (tokenManager.isRefreshTokenRevoked(token)) {
      throw new RefreshTokenAlreadyRevokedException(
          MessageFormat.format("Token <{0}> is revoked>", token));
    }
    var authentication =
        refreshTokenProvider.authenticate(new BearerTokenAuthenticationToken(token));
    return ResponseEntity.status(HttpStatus.OK)
        .contentType(APPLICATION_JSON)
        .body(tokenManager.generateTokenPair(authentication));
  }

  @Operation(
      summary = "Revoke current refresh JWT",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully revoke refresh JWT",
            content = {
              @Content(
                  mediaType = APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = AppResponse.class))
            }),
        @ApiResponse(
            responseCode = "401",
            description = "Provided refresh JWT is <Invalid> or <Expired>",
            content = {
              @Content(
                  mediaType = APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorResponse.class))
            }),
        @ApiResponse(
            responseCode = "409",
            description = "JWT already revoked",
            content = {
              @Content(
                  mediaType = APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorResponse.class))
            }),
        @ApiResponse(
            responseCode = "500",
            description = "INTERNAL_SERVER_ERROR",
            content = {
              @Content(
                  mediaType = APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorResponse.class))
            })
      })
  @PostMapping("/token/revoke")
  public ResponseEntity<AppResponse> revokeToken(@RequestBody UserTokenPair refreshToken)
      throws BadCredentialsException, RefreshTokenAlreadyRevokedException {
    String token = refreshToken.getRefreshToken();
    var authentication =
        refreshTokenProvider.authenticate(new BearerTokenAuthenticationToken(token));
    tokenManager.revokeToken(authentication);
    var resp =
        new AppResponse(
            HttpStatus.OK.value(), String.format("Token <%s> successfully revoked", token));
    return ResponseEntity.status(HttpStatus.OK).body(resp);
  }

  @Operation(
      summary = "Send reset password link by email",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = {
              @Content(
                  mediaType = APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = MailResetPasswordResponse.class))
            }),
        @ApiResponse(
            responseCode = "400",
            description = "BAD_REQUEST",
            content = {
              @Content(
                  mediaType = APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorResponse.class))
            }),
        @ApiResponse(
            responseCode = "404",
            description = "NOT_FOUND",
            content = {
              @Content(
                  mediaType = APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorResponse.class))
            }),
        @ApiResponse(
            responseCode = "500",
            description = "INTERNAL_SERVER_ERROR",
            content = {
              @Content(
                  mediaType = APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorResponse.class))
            })
      })
  @PostMapping(path = "/login/resetPassword")
  public ResponseEntity<MailResetPasswordResponse> sendResetPasswordEmail(
      @Valid @RequestBody MailResetPasswordRequest mailResetPasswordRequest) {
    var user = userService.findUserByEmail(mailResetPasswordRequest.getEmail());
    String token = UUID.randomUUID().toString();
    userService.createPasswordResetTokenForUser(user, token);
    emailUtils.sendResetPasswordEmail(token, user);
    return ResponseEntity.status(HttpStatus.OK)
        .body(new MailResetPasswordResponse(LocalDateTime.now(), String.valueOf(user.getId())));
  }

  @Operation(
      summary = "Reset user password",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = {
              @Content(
                  mediaType = APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = TokenPair.class))
            }),
        @ApiResponse(
            responseCode = "400",
            description = "BAD_REQUEST",
            content = {
              @Content(
                  mediaType = APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorResponse.class))
            }),
        @ApiResponse(
            responseCode = "404",
            description = "NOT_FOUND",
            content = {
              @Content(
                  mediaType = APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorResponse.class))
            }),
        @ApiResponse(
            responseCode = "500",
            description = "INTERNAL_SERVER_ERROR",
            content = {
              @Content(
                  mediaType = APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorResponse.class))
            })
      })
  @PostMapping(path = "/login/resetPassword/savePassword")
  public ResponseEntity<TokenPair> resetPassword(@Valid @RequestBody PasswordResetRequest request) {
    var passwordResetToken = userService.getPasswordResetToken(request.getResetToken());
    passwordResetToken.validate(passwordResetToken);
    var user = userService.getUserByPasswordResetToken(request.getResetToken());
    userService.changeUserPassword(user.getId(), request.getPassword());
    var authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(user.getEmail(), request.getPassword()));
    return ResponseEntity.status(HttpStatus.OK)
        .body(tokenManager.generateTokenPair(authentication));
  }
}
