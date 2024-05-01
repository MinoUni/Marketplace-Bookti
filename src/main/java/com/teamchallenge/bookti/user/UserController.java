package com.teamchallenge.bookti.user;

import static com.teamchallenge.bookti.config.SwaggerConfig.USER_UPDATE_REQ_SCHEMA;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.security.authentication.UsernamePasswordAuthenticationToken.authenticated;

import com.teamchallenge.bookti.dto.AppResponse;
import com.teamchallenge.bookti.dto.ErrorResponse;
import com.teamchallenge.bookti.exception.user.RefreshTokenAlreadyRevokedException;
import com.teamchallenge.bookti.security.jwt.TokenManager;
import com.teamchallenge.bookti.user.dto.MailResetPasswordRequest;
import com.teamchallenge.bookti.user.dto.MailResetPasswordResponse;
import com.teamchallenge.bookti.user.dto.NewUserRegistrationRequest;
import com.teamchallenge.bookti.user.dto.PasswordResetRequest;
import com.teamchallenge.bookti.user.dto.TokenPair;
import com.teamchallenge.bookti.user.dto.UserProfileDTO;
import com.teamchallenge.bookti.user.dto.UserLoginRequest;
import com.teamchallenge.bookti.user.dto.UserTokenPair;
import com.teamchallenge.bookti.user.dto.UserUpdateReq;
import com.teamchallenge.bookti.utils.EmailUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Tag(name = "User endpoints")
class UserController {

  private final UserService userService;
  private final TokenManager tokenManager;
  private final AuthenticationManager authenticationManager;
  private final EmailUtils emailUtils;
  private final JwtAuthenticationProvider refreshTokenProvider;

  public UserController(
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
      path = "/authorize/signup",
      consumes = APPLICATION_JSON_VALUE,
      produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<TokenPair> signup(@Valid @RequestBody NewUserRegistrationRequest newUser) {
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
  @PostMapping(path = "/authorize/login")
  public ResponseEntity<TokenPair> login(@Valid @RequestBody UserLoginRequest credentials)
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
  @PostMapping("/authorize/token/refresh")
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
  @PostMapping("/authorize/token/revoke")
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
  @PostMapping(path = "/authorize/login/resetPassword")
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
  @PostMapping(path = "/authorize/login/resetPassword/savePassword")
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

  @Operation(
      summary = "Find User information",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "User information found",
            content = {
              @Content(
                  mediaType = APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = UserProfileDTO.class))
            }),
        @ApiResponse(
            responseCode = "401",
            description = "UNAUTHORIZED",
            content = {
              @Content(
                  mediaType = APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorResponse.class))
            }),
        @ApiResponse(
            responseCode = "403",
            description = "ACCESS_DENIED",
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
  @GetMapping(path = "/users/{id}")
  @PreAuthorize("isAuthenticated() and hasRole('ROLE_USER') and authentication.principal.id == #id")
  public ResponseEntity<UserProfileDTO> getUserInfo(@PathVariable Integer id) {
    return ResponseEntity.status(HttpStatus.OK).body(userService.findById(id));
  }

  @Operation(
      summary = "Update user information",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "User information updated",
            content = {
              @Content(
                  mediaType = APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = UserProfileDTO.class))
            }),
        @ApiResponse(
            responseCode = "401",
            description = "UNAUTHORIZED",
            content = {
              @Content(
                  mediaType = APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorResponse.class))
            }),
        @ApiResponse(
            responseCode = "403",
            description = "ACCESS_DENIED",
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
  @PatchMapping(
      value = "/users/{id}",
      consumes = {APPLICATION_JSON_VALUE, MULTIPART_FORM_DATA_VALUE},
      produces = APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated() and hasRole('ROLE_USER') and authentication.principal.id == #id")
  public ResponseEntity<UserProfileDTO> updateUserInfo(
      @PathVariable Integer id,
      @Parameter(description = USER_UPDATE_REQ_SCHEMA, required = true) @RequestPart("user_update")
          UserUpdateReq userUpdateInfo,
      @RequestPart(value = "image", required = false) final MultipartFile image) {
    return ResponseEntity.ok(userService.updateUserInfo(id, userUpdateInfo, image));
  }

  @Operation()
  @PostMapping(value = "/users/{userId}/wishlist", produces = APPLICATION_JSON_VALUE)
  @PreAuthorize(
      "isAuthenticated() and hasRole('ROLE_USER') and authentication.principal.id == #userId")
  public ResponseEntity<AppResponse> addBookToWishlist(
      @PathVariable Integer userId, @RequestParam("bookId") Integer bookId) {
    return ResponseEntity.ok(userService.addBookToWishlist(userId, bookId));
  }

  @Operation()
  @DeleteMapping(value = "/users/{userId}/wishlist", produces = APPLICATION_JSON_VALUE)
  @PreAuthorize(
      "isAuthenticated() and hasRole('ROLE_USER') and authentication.principal.id == #userId")
  public ResponseEntity<AppResponse> deleteBookFromWishlist(
      @PathVariable Integer userId, @RequestParam("bookId") Integer bookId) {
    return ResponseEntity.ok(userService.deleteBookFromWishlist(userId, bookId));
  }
}
