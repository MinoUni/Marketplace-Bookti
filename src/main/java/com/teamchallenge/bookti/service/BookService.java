package com.teamchallenge.bookti.service;

import com.teamchallenge.bookti.dto.book.BookDetails;
import com.teamchallenge.bookti.dto.book.BookProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Interface that describes books management
 *
 * @author MinoUni
 * @version 1.0
 */
// todo: Implement interface
public interface BookService {

  /**
   * The method to add a new book to a user book list.
   *
   * @param bookProfile DTO with book details
   * @return DTO of create book
   */
  BookDetails create(BookProfile bookProfile);

  /**
   * The method to find book by id.
   *
   * @param id book identifier
   * @return book DTO
   */
  BookDetails findById(UUID id);

  /**
   * The method to find all books and slice it.
   *
   * @param pageable pagination
   * @return page of books
   */
  Page<BookDetails> findAll(Pageable pageable);
}
