package com.teamchallenge.bookti.book;

import com.teamchallenge.bookti.user.dto.UserDTO;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {

  @Query(
      """
        SELECT new com.teamchallenge.bookti.book.BookDetailsDTO(
          b.id, b.title, b.author, b.genre, b.publicationYear,
          b. language, b.exchangeFormat, b.imageUrl, b.description)
        FROM Book b
        WHERE b.id = :id
      """)
  Optional<BookDetailsDTO> findBookById(@Param("id") Integer id);

  @Query(
      """
        SELECT new com.teamchallenge.bookti.user.dto.UserDTO(
          u.id, u.fullName, u.email, u.creationDate, u.location,
          u.displayEmail, u.telegramId, u.displayTelegram, u.avatarUrl)
        FROM Book b
        INNER JOIN b.owner u
        WHERE b.id = :id
      """)
  UserDTO getBookOwner(@Param("id") Integer id);

  @Query(
      """
        SELECT new com.teamchallenge.bookti.book.BookDetailsDTO(
          b.id, b.title, b.author, b.genre, b.publicationYear,
          b. language, b.exchangeFormat, b.imageUrl, b.description)
        FROM Book b
      """)
  Page<BookDetailsDTO> findAllBooks(Pageable pageable);

  @Query(
      """
        SELECT new com.teamchallenge.bookti.book.BookProfileDTO(
          b.id, b.title, b.author, b.language, b.imageUrl)
        FROM Book b
        INNER JOIN b.owner u
        WHERE u.id = :user_id
      """)
  Set<BookProfileDTO> getAllUserBooks(@Param("user_id") Integer userId);

  @Query(
      """
        SELECT new com.teamchallenge.bookti.book.BookProfileDTO(
          b.id, b.title, b.author, b.language, b.imageUrl)
        FROM Book b
        INNER JOIN b.candidates u
        WHERE u.id = :user_id
      """)
  Set<BookProfileDTO> getUserWishlist(@Param("user_id") Integer userId);
}
