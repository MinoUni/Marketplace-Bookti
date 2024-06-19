package com.teamchallenge.bookti.book;

import static com.teamchallenge.bookti.utils.CloudinaryUtils.BOOKS_FOLDER_NAME;
import static org.springframework.transaction.annotation.Isolation.REPEATABLE_READ;

import com.teamchallenge.bookti.constant.BookConstant;
import com.teamchallenge.bookti.constant.UserConstant;
import com.teamchallenge.bookti.exception.book.BookNotFoundException;
import com.teamchallenge.bookti.exception.user.UserNotFoundException;
import com.teamchallenge.bookti.mapper.BookMapper;
import com.teamchallenge.bookti.user.User;
import com.teamchallenge.bookti.user.UserRepository;
import com.teamchallenge.bookti.user.dto.UserDTO;
import com.teamchallenge.bookti.utils.CloudinaryUtils;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

  private final BookRepository bookRepository;
  private final UserRepository userRepository;
  private final CloudinaryUtils cloudinaryUtils;
  private final BookMapper bookMapper;

  @Transactional
  public int save(BookSaveDTO bookDto, MultipartFile image, Integer userId) {
    if (!userRepository.existsById(userId)) {
      throw new UserNotFoundException(String.format(UserConstant.NOT_FOUND_MESSAGE, userId));
    }
    User user = userRepository.getReferenceById(userId);
    Book book = bookMapper.mapToBook(bookDto);
    book.setOwner(user);
    uploadImageIfPresent(image, book);
    book = bookRepository.save(book);
    return book.getId();
  }

  public BookDetailsDTO findById(Integer id) {
    BookDetailsDTO book =
        bookRepository
            .findBookById(id)
            .orElseThrow(
                () -> new BookNotFoundException(String.format(BookConstant.NOT_FOUND, id)));
    UserDTO owner = bookRepository.getBookOwner(id);
    book.setOwner(owner);
    return book;
  }

  public Page<BookDetailsDTO> findAll(Pageable pageable) {
    return bookRepository.findAllBooks(pageable);
  }

  public Page<BookDetailsDTO> findAllBooksWithPendingApprovalStatus(Pageable pageable) {
    List<Book> books = bookRepository.findAllByStatus(BookStatus.PENDING_APPROVAL);
    List<BookDetailsDTO> booksDto = bookMapper.mapToBookDetailsDTO(books);
    return new PageImpl<>(booksDto, pageable, booksDto.size());
  }

  @Transactional
  public void deleteById(Integer id) {
    if (!bookRepository.existsById(id)) {
      throw new BookNotFoundException(String.format(BookConstant.NOT_FOUND, id));
    }
    bookRepository.deleteById(id);
  }

  @Transactional(isolation = REPEATABLE_READ)
  public BookDetailsDTO updateById(Integer id, BookUpdateReq bookUpdate, MultipartFile image) {
    var book =
        bookRepository
            .findById(id)
            .orElseThrow(
                () -> new BookNotFoundException(String.format(BookConstant.NOT_FOUND, id)));
    if (image != null && !image.isEmpty()) {
      var imageUrl = cloudinaryUtils.uploadFile(image, book.getImageName(), BOOKS_FOLDER_NAME);
      book.setImageUrl(imageUrl);
    }
    bookMapper.mapToBook(bookUpdate, book);
    bookRepository.save(book);
    return findById(id);
  }

  @Transactional(isolation = REPEATABLE_READ)
  public void updateBookStatusById(Integer id, String status) {
    if (!bookRepository.existsById(id)) {
      throw new BookNotFoundException(String.format(BookConstant.NOT_FOUND, id));
    }
    bookRepository.updateStatusById(id, BookStatus.findStatus(status));
  }

  private void uploadImageIfPresent(MultipartFile image, Book book) {
    if (image == null || image.isEmpty()) {
      return;
    }
    String imageName = UUID.randomUUID().toString();
    book.setImageName(imageName);
    String imageUrl = cloudinaryUtils.uploadFile(image, imageName, BOOKS_FOLDER_NAME);
    book.setImageUrl(imageUrl);
  }
}
