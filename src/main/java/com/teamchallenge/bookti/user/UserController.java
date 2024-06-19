package com.teamchallenge.bookti.user;

import static com.teamchallenge.bookti.config.SwaggerConfig.USER_UPDATE_REQ_SCHEMA;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

import com.teamchallenge.bookti.dto.AppResponse;
import com.teamchallenge.bookti.dto.ErrorResponse;
import com.teamchallenge.bookti.user.dto.UserProfileDTO;
import com.teamchallenge.bookti.user.dto.UserUpdateDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Tag(name = "User")
@RequiredArgsConstructor
@RequestMapping("/users")
class UserController {

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
  @GetMapping(path = "/{id}")
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
      value = "/{id}",
      consumes = {APPLICATION_JSON_VALUE, MULTIPART_FORM_DATA_VALUE},
      produces = APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated() and hasRole('ROLE_USER') and authentication.principal.id == #id")
  public ResponseEntity<UserProfileDTO> updatePartially(
      @PathVariable Integer id,
      @Parameter(description = USER_UPDATE_REQ_SCHEMA, required = true) @RequestPart("user_update")
          UserUpdateDto userUpdateInfo,
      @RequestPart(value = "image", required = false) final MultipartFile image) {
    return ResponseEntity.ok(userService.updateUserInfo(id, userUpdateInfo, image));
  }

  @Operation()
  @PostMapping(value = "/{userId}/wishlist", produces = APPLICATION_JSON_VALUE)
  @PreAuthorize(
      "isAuthenticated() and hasRole('ROLE_USER') and authentication.principal.id == #userId")
  public ResponseEntity<AppResponse> addBookToWishlist(
      @PathVariable Integer userId, @RequestParam("bookId") Integer bookId) {
    return ResponseEntity.ok(userService.addBookToWishlist(userId, bookId));
  }

  @Operation()
  @DeleteMapping(value = "/{userId}/wishlist", produces = APPLICATION_JSON_VALUE)
  @PreAuthorize(
      "isAuthenticated() and hasRole('ROLE_USER') and authentication.principal.id == #userId")
  public ResponseEntity<AppResponse> deleteBookFromWishlist(
      @PathVariable Integer userId, @RequestParam("bookId") Integer bookId) {
    return ResponseEntity.ok(userService.deleteBookFromWishlist(userId, bookId));
  }

  @Operation()
  @GetMapping(value = "/new", produces = APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated() and hasRole('ROLE_USER')")
  public ResponseEntity<List<UserProfileDTO>> findAllNewUsers() {
    return ResponseEntity.ok(userService.findAllNewUsers());
  }
}
