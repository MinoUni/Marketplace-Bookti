package com.teamchallenge.bookti.user;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.teamchallenge.bookti.book.BookService;
import com.teamchallenge.bookti.constant.BookConstant;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Admin")
@RequestMapping("/admin")
@PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
class AdminController {

  private final BookService bookService;

  @Operation
  @GetMapping(
      value = "/books",
      produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<Page<?>> findBooksWithPendingApprovalStatus(final Pageable pageable) {
    return ResponseEntity.ok()
        .contentType(APPLICATION_JSON)
        .body(bookService.findAllBooksWithPendingApprovalStatus(pageable));
  }

  @Operation
  @PostMapping(
      value = "/reviews",
      produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<Page<?>> findReviewsWithPendingApprovalStatus(final Pageable pageable) {
    // todo: implement after review feature merge
    return ResponseEntity.ok().contentType(APPLICATION_JSON).build();
  }

  @Operation
  @PatchMapping(
      value = "/books/{id}",
      produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<String> changeBookStatus(
      @PathVariable Integer id, @RequestParam("status") String status) {
    bookService.updateBookStatusById(id, status);
    return ResponseEntity.ok()
        .contentType(APPLICATION_JSON)
        .body(String.format(BookConstant.BOOK_STATUS_UPDATE, id));
  }

  @Operation
  @PatchMapping(
      value = "/reviews/{id}",
      produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<String> changeReviewStatus(@PathVariable Integer id) {
    // todo: implement after review feature merge
    return ResponseEntity.ok().contentType(APPLICATION_JSON).build();
  }
}
