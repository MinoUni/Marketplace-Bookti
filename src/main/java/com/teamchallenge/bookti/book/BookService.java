package com.teamchallenge.bookti.book;

import static com.teamchallenge.bookti.utils.CloudinaryUtils.BOOKS_FOLDER_NAME;
import static org.springframework.transaction.annotation.Isolation.REPEATABLE_READ;

import com.teamchallenge.bookti.constant.BookConstant;
import com.teamchallenge.bookti.constant.UserConstant;
import com.teamchallenge.bookti.exception.book.BookNotFoundException;
import com.teamchallenge.bookti.exception.user.UserNotFoundException;
import com.teamchallenge.bookti.mapper.BookMapper;
import com.teamchallenge.bookti.user.User;
import com.teamchallenge.bookti.user.UserDTO;
import com.teamchallenge.bookti.user.UserRepository;
import com.teamchallenge.bookti.utils.CloudinaryUtils;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
class BookService {

  private final BookRepository bookRepository;
  private final UserRepository userRepository;
  private final CloudinaryUtils cloudinaryUtils;
  private final BookMapper bookMapper;

  @Transactional
  public int save(BookSaveDTO bookDto, MultipartFile image, Integer userId) {
    String imageUrl = null;
    String imageName = null;
    if (!userRepository.existsById(userId)) {
      throw new UserNotFoundException(String.format(UserConstant.NOT_FOUND_MESSAGE, userId));
    }
    User user = userRepository.getReferenceById(userId);
    Book book = bookMapper.mapBookSaveDtoAndUserToBook(bookDto, user);
    if (image != null && !image.isEmpty()) {
      imageName = UUID.randomUUID().toString();
      imageUrl = cloudinaryUtils.uploadFile(image, imageName, BOOKS_FOLDER_NAME);
      book.setImageName(imageName);
      book.setImageUrl(imageUrl);
    }
    book = bookRepository.save(book);
    return book.getId();
  }

  public BookDetailsDTO findById(Integer id) {
    BookDetailsDTO book =
        bookRepository
            .findBookById(id)
            .orElseThrow(
                () -> new BookNotFoundException(String.format(BookConstant.NOT_FOUND_MESSAGE, id)));
    UserDTO owner = bookRepository.getBookOwner(id);
    book.setOwner(owner);
    return book;
  }

  public Page<BookDetailsDTO> findAll(Pageable pageable) {
    return bookRepository.findAllBooks(pageable);
  }

  @Transactional
  public void deleteById(Integer id) {
    if (!bookRepository.existsById(id)) {
      throw new BookNotFoundException(String.format(BookConstant.NOT_FOUND_MESSAGE, id));
    }
    bookRepository.deleteById(id);
  }

  @Transactional(isolation = REPEATABLE_READ)
  public BookDetailsDTO updateById(Integer id, BookUpdateReq bookUpdate, MultipartFile image) {
    if (!bookRepository.existsById(id)) {
      throw new BookNotFoundException(String.format(BookConstant.NOT_FOUND_MESSAGE, id));
    }
    var book = bookRepository.getReferenceById(id);
    if (image != null && !image.isEmpty()) {
      var imageUrl = cloudinaryUtils.uploadFile(image, book.getImageName(), BOOKS_FOLDER_NAME);
      book.setImageUrl(imageUrl);
    }
    bookMapper.mapBookUpdateToBook(bookUpdate, book);
    bookRepository.save(book);
    return findById(id);
  }
}
