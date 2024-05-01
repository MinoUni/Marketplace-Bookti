package com.teamchallenge.bookti.book;

import static com.teamchallenge.bookti.utils.CloudinaryUtils.BOOKS_FOLDER_NAME;
import static org.springframework.transaction.annotation.Isolation.REPEATABLE_READ;

import com.teamchallenge.bookti.exception.book.BookNotFoundException;
import com.teamchallenge.bookti.exception.user.UserNotFoundException;
import com.teamchallenge.bookti.mapper.BookMapper;
import com.teamchallenge.bookti.mapper.MapperUtils;
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
  private final MapperUtils mapperUtils;
  private final BookMapper bookMapper;

  @Transactional
  public BookDetailsDTO save(BookSaveDTO bookSaveDTO, MultipartFile image, Integer userId) {
    String imageUrl = null;
    String imageName = UUID.randomUUID().toString();
    if (image != null && !image.isEmpty()) {
      imageUrl = cloudinaryUtils.uploadFile(image, imageName, BOOKS_FOLDER_NAME);
    }
    var user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found."));
    var book = build(bookSaveDTO, imageUrl, imageName, user);
    book = bookRepository.save(book);
    return bookMapper.mapBookToBookDetailsDTO(book);
  }

  public BookDetailsDTO findById(Integer id) {
    BookDetailsDTO book =
        bookRepository
            .findBookById(id)
            .orElseThrow(() -> new BookNotFoundException("Book not found."));
    UserDTO owner = bookRepository.getBookOwner(book.getId());
    book.setOwner(owner);
    return book;
  }

  public Page<BookDetailsDTO> findAll(Pageable pageable) {
    return bookRepository.findAllBooks(pageable);
  }

  @Transactional
  public void deleteById(Integer id) {
    if (bookRepository.existsById(id)) {
      bookRepository.deleteById(id);
    }
    throw new BookNotFoundException("Book not found.");
  }

  @Transactional(isolation = REPEATABLE_READ)
  public BookDetailsDTO updateById(
      Integer id, BookUpdateReq bookUpdateInfo, MultipartFile imageFile) {
    if (!bookRepository.existsById(id)) {
      throw new BookNotFoundException("Book not found.");
    }
    var book = bookRepository.getReferenceById(id);
    if (imageFile != null && !imageFile.isEmpty()) {
      var imageUrl = cloudinaryUtils.uploadFile(imageFile, book.getImageName(), BOOKS_FOLDER_NAME);
      book.setImageUrl(imageUrl);
    }
    mapperUtils.mapBookUpdateToBook(bookUpdateInfo, book);
    bookRepository.save(book);
    return findById(id);
  }

  private Book build(BookSaveDTO bookSaveDTO, String imageUrl, String imageName, User user) {
    return Book.builder()
        .title(bookSaveDTO.getTitle())
        .author(bookSaveDTO.getAuthor())
        .genre(bookSaveDTO.getGenre())
        .description(bookSaveDTO.getDescription())
        .imageName(imageName)
        .imageUrl(imageUrl)
        .language(bookSaveDTO.getLanguage())
        .publicationYear(bookSaveDTO.getPublicationYear())
        .exchangeFormat(BookExchangeFormat.valueOf(bookSaveDTO.getExchangeFormat()))
        .owner(user)
        .build();
  }
}
