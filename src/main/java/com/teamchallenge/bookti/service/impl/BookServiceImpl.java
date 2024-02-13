package com.teamchallenge.bookti.service.impl;

import com.teamchallenge.bookti.dto.book.BookDetails;
import com.teamchallenge.bookti.dto.book.BookProfile;
import com.teamchallenge.bookti.exception.BookNotFoundException;
import com.teamchallenge.bookti.exception.UserNotFoundException;
import com.teamchallenge.bookti.model.Book;
import com.teamchallenge.bookti.repository.BookRepository;
import com.teamchallenge.bookti.repository.UserRepository;
import com.teamchallenge.bookti.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

  private final BookRepository bookRepository;
  private final UserRepository userRepository;

  @Transactional
  @Override
  public BookDetails create(BookProfile bookProfile, MultipartFile imageFile) {
    String imageUrl = null;
    if (imageFile != null && !imageFile.isEmpty()) {
      // todo: import and implement cloudinary API
      // imageUrl = cloudinary.upload(imageFile, fileName);
    }
    var user = userRepository.findById(bookProfile.getUserId())
            .orElseThrow(() -> new UserNotFoundException("User not found."));
    var book = Book.build(bookProfile, imageUrl, user);
    book = bookRepository.save(book);
    return BookDetails.build(book);
  }

  @Transactional(readOnly = true)
  @Override
  public BookDetails findById(UUID id) {
    return bookRepository.findBookById(id)
        .orElseThrow(() -> new BookNotFoundException("Book not found."));
  }

  @Transactional(readOnly = true)
  @Override
  public Page<BookDetails> findAll(Pageable pageable) {
    return bookRepository.findAllByPageable(pageable);
  }
}
