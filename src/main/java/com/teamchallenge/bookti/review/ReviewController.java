package com.teamchallenge.bookti.review;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.teamchallenge.bookti.dto.AppResponse;
import com.teamchallenge.bookti.dto.ErrorResponse;
import com.teamchallenge.bookti.security.AuthorizedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Review endpoints")
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/reviews")
@Validated
class ReviewController {

  private final UserReviewService userReviewService;

  @Operation(
      summary = "Find User reviews",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "User reviews information found",
            content = {
              @Content(
                  mediaType = APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = UserReview.class))
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
  public ResponseEntity<List<UserReview>> findAllUserReceivedReviewsById(
      @RequestParam("userId") Integer userId) {
    List<UserReview> response = userReviewService.findAllUserReceivedReviewsById(userId);
    log.info(
        "From ReviewController method findAllUserReceivedReviewsById  - /reviews/ - return user reviews. ");
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @Operation(
      summary = "Leave a review about the user",
      responses = {
        @ApiResponse(
            responseCode = "201",
            description = "User reviews information found",
            content = {
              @Content(
                  mediaType = APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = UserReviewResponseDTO.class))
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
  public ResponseEntity<UserReviewResponseDTO> save(
      @AuthenticationPrincipal AuthorizedUser authorizedUser,
      @Valid @RequestBody UserReviewSaveDTO userReview) {

    UserReviewResponseDTO response = userReviewService.save(userReview, authorizedUser.getId());
    log.info(
        "From ReviewController method  save - /reviews - Saved a new review about the user {}",
        userReview.getOwnerId());
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @Operation(
      summary = "Delete a review about the user",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "User Review deleted",
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
  @PreAuthorize("isAuthenticated() and hasRole('ROLE_USER')")
  @DeleteMapping("/{reviewId}")
  public ResponseEntity<AppResponse> deleteById(@PathVariable("reviewId") Integer reviewId) {
    String responseMessage = userReviewService.deleteById(reviewId);
    AppResponse response = new AppResponse(OK.value(), responseMessage);
    log.info(
        "From ReviewController method delete - /reviews/{reviewId} - delete a review about the user {}",
        reviewId);

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
