package com.teamchallenge.bookti.repository;

import com.teamchallenge.bookti.dto.book.BookDetails;
import com.teamchallenge.bookti.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {

  @Query(
          """
            SELECT new com.teamchallenge.bookti.dto.book.BookDetails(
            b.id, b.title, b.author, b.genre, b.publicationDate,
            b. language, b.tradeFormat, b.imageUrl, b.description)
            FROM Book b
          """)
  Optional<BookDetails> findBookById(UUID id);

  @Query(
      """
        SELECT new com.teamchallenge.bookti.dto.book.BookDetails(
        b.id, b.title, b.author, b.genre, b.publicationDate,
        b. language, b.tradeFormat, b.imageUrl, b.description)
        FROM Book b
      """)
  Page<BookDetails> findAllByPageable(Pageable pageable);
}
