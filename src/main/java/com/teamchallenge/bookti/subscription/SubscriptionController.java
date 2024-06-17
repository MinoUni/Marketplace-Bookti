package com.teamchallenge.bookti.subscription;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.teamchallenge.bookti.dto.AppResponse;
import com.teamchallenge.bookti.dto.ErrorResponse;
import com.teamchallenge.bookti.security.AuthorizedUser;
import com.teamchallenge.bookti.user.dto.UserSubscriptionDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Subscription endpoints")
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/subscriptions")
public class SubscriptionController {

  private final SubscriptionService subscriptionService;

  @Operation(
      summary = "Find User subscriptions",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "User subscriptions information found",
              content = {
                  @Content(
                      mediaType = APPLICATION_JSON_VALUE,
                      schema = @Schema(implementation = UserSubscriptionDTO.class))
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
  @GetMapping
  @PreAuthorize("isAuthenticated() and hasRole('ROLE_USER')")
  public ResponseEntity<List<UserSubscriptionDTO>> findAllUserSubscriptionById(
      @RequestParam("userId") Integer userId) {
    List<UserSubscriptionDTO> response = subscriptionService.findAllUserSubscriptionById(userId);
    log.info(
        "SubscriptionController::findAllUserSubscriptionById - Get /subscriptions -"
            + " return list or empty list, received review to user: {}.", userId);
    return ResponseEntity.ok(response);
  }

  @Operation(
      summary = "Make subscription to user",
      responses = {
          @ApiResponse(
              responseCode = "201",
              description = "Make subscriptions to user",
              content = {
                  @Content(
                      mediaType = APPLICATION_JSON_VALUE,
                      schema = @Schema(implementation = AppResponse.class))
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
  @PostMapping
  @PreAuthorize("isAuthenticated() and hasRole('ROLE_USER')")
  public ResponseEntity<AppResponse> save(@AuthenticationPrincipal AuthorizedUser authorizedUser,
                                          @RequestParam("subscriberId") Integer subscriberId) {
    String responseMessage = subscriptionService.save(authorizedUser.getId(), subscriberId);
    AppResponse response = new AppResponse(CREATED.value(), responseMessage);
    log.info(
        "SubscriptionController::save - Post /subscriptions - return successfully subscription message.");
    return ResponseEntity.status(CREATED).body(response);
  }

  @GetMapping("/status")
  @PreAuthorize("isAuthenticated() and hasRole('ROLE_USER')")
  public ResponseEntity<Boolean> checkIfUserIsSubscribed(
      @AuthenticationPrincipal AuthorizedUser authorizedUser,
      @RequestParam("subscriberId") Integer subscriberId) {
    Boolean st = subscriptionService.checkIfUserIsSubscribed(authorizedUser.getId(), subscriberId);
    log.info(
        "SubscriptionController::checkIfUserIsSubscribed - Get /subscriptions/status -"
            + " return Boolean info about subscription.");
    return ResponseEntity.ok(st);
  }

  @Operation(
      summary = "Delete a subscription to user",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "User subscription deleted",
              content = {
                  @Content(
                      mediaType = APPLICATION_JSON_VALUE,
                      schema = @Schema(implementation = AppResponse.class))
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
  @DeleteMapping("/{subscriptionId}")
  @PreAuthorize("isAuthenticated() and hasRole('ROLE_USER')")
  public ResponseEntity<AppResponse> deleteById(@PathVariable("subscriptionId") Integer subscriptionId) {
    String responseMessage = subscriptionService.deleteById(subscriptionId);
    AppResponse response = new AppResponse(OK.value(), responseMessage);
    log.info(
        "SubscriptionController::deleteById - Delete /subscriptions/{subscriptionId} -"
            + " return successfully deleted subscription message.");
    return ResponseEntity.ok(response);
  }
}
