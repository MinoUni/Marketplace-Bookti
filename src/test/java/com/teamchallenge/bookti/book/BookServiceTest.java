package com.teamchallenge.bookti.book;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.teamchallenge.bookti.exception.book.BookNotFoundException;
import com.teamchallenge.bookti.exception.user.UserNotFoundException;
import com.teamchallenge.bookti.mapper.BookMapper;
import com.teamchallenge.bookti.user.User;
import com.teamchallenge.bookti.user.UserDTO;
import com.teamchallenge.bookti.user.UserRepository;
import com.teamchallenge.bookti.utils.CloudinaryUtils;
import java.time.Year;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

  private final BookRepository mockBookRepository = mock(BookRepository.class);

  private final UserRepository mockUserRepository = mock(UserRepository.class);

  private final CloudinaryUtils mockCloudinary = mock(CloudinaryUtils.class);

  private final BookMapper mockBookMapper = mock(BookMapper.class);

  private final BookService bookService =
      new BookService(mockBookRepository, mockUserRepository, mockCloudinary, mockBookMapper);

  @Test
  @DisplayName("when save book without image then return saved book id")
  void whenBookSaveWithoutImageThenSaveBookAndReturnBookId() {
    MultipartFile image = null;
    int userId = 1;
    BookSaveDTO bookPayload = generateValidBookSaveDto();
    User user = generateUser(userId);
    Book book = generateValidBook(user);

    when(mockUserRepository.existsById(eq(userId))).thenReturn(true);
    when(mockUserRepository.getReferenceById(eq(userId))).thenReturn(user);
    when(mockBookMapper.mapBookSaveDtoAndUserToBook(eq(bookPayload), eq(user))).thenReturn(book);
    when(mockBookRepository.save(eq(book))).thenReturn(book);

    assertDoesNotThrow(() -> bookService.save(bookPayload, image, userId));

    verify(mockUserRepository, times(1)).existsById(eq(userId));
    verify(mockUserRepository, times(1)).getReferenceById(eq(userId));
    verify(mockBookMapper, times(1)).mapBookSaveDtoAndUserToBook(eq(bookPayload), eq(user));
    verify(mockCloudinary, never()).uploadFile(eq(image), any(), any());
    verify(mockBookRepository, times(1)).save(eq(book));
  }

  @Test
  @DisplayName("when non-existing user try to save book then throw UserNotFoundException")
  void whenBookSaveWithNonExistingUserThenThrowUserNotFoundException() {
    MultipartFile image = null;
    int userId = 1;
    BookSaveDTO bookPayload = generateValidBookSaveDto();

    when(mockUserRepository.existsById(eq(userId))).thenReturn(false);

    assertThrows(UserNotFoundException.class, () -> bookService.save(bookPayload, image, userId));

    verify(mockUserRepository, times(1)).existsById(eq(userId));
    verify(mockUserRepository, never()).getReferenceById(eq(userId));
    verify(mockBookMapper, never()).mapBookSaveDtoAndUserToBook(eq(bookPayload), any(User.class));
    verify(mockCloudinary, never()).uploadFile(eq(image), any(String.class), any(String.class));
    verify(mockBookRepository, never()).save(any(Book.class));
  }

  @Test
  @DisplayName(
      "when save book with image, upload file to cloud, save link with book into db, then return book id")
  void whenBookSaveWithImageThenUploadImageToCloudAndSaveBookAndLinkIntoDatabase() {
    MultipartFile image =
        new MockMultipartFile("image", "image", MediaType.IMAGE_JPEG_VALUE, new byte[] {0, 1, 1});
    int userId = 1;
    BookSaveDTO bookPayload = generateValidBookSaveDto();
    String imageUrl = "image_url";
    User user = generateUser(userId);
    Book book = generateValidBook(user);

    when(mockUserRepository.existsById(eq(userId))).thenReturn(true);
    when(mockUserRepository.getReferenceById(eq(userId))).thenReturn(user);
    when(mockCloudinary.uploadFile(eq(image), any(String.class), any(String.class)))
        .thenReturn(imageUrl);
    when(mockBookMapper.mapBookSaveDtoAndUserToBook(eq(bookPayload), eq(user))).thenReturn(book);
    when(mockBookRepository.save(eq(book))).thenReturn(book);

    assertDoesNotThrow(() -> bookService.save(bookPayload, image, userId));

    verify(mockUserRepository, times(1)).existsById(eq(userId));
    verify(mockUserRepository, times(1)).getReferenceById(eq(userId));
    verify(mockBookMapper, times(1)).mapBookSaveDtoAndUserToBook(eq(bookPayload), eq(user));
    verify(mockCloudinary, times(1)).uploadFile(eq(image), any(String.class), any(String.class));
    verify(mockBookRepository, times(1)).save(eq(book));
  }

  @Test
  @DisplayName("when find book by id, then return that book details dto")
  void whenFindBookByIdThenReturnBookDetailsDto() {
    final int userId = 1;
    final int bookId = 1;
    BookDetailsDTO bookDto = BookDetailsDTO.builder().build();
    UserDTO userDto = UserDTO.builder().build();

    when(mockBookRepository.findBookById(eq(bookId))).thenReturn(Optional.of(bookDto));
    when(mockBookRepository.getBookOwner(eq(userId))).thenReturn(userDto);

    assertDoesNotThrow(() -> bookService.findById(bookId));

    verify(mockBookRepository, times(1)).findBookById(eq(bookId));
    verify(mockBookRepository, times(1)).getBookOwner(eq(userId));
  }

  @Test
  @DisplayName("when find non-existing book by id, then throw BookNotFoundException")
  void whenFindNonExistingBookByIdThenTrowBookNotFoundException() {
    final int userId = 1;
    final int bookId = 1;
    final String message = String.format("Book with id [%d] not found.", bookId);

    when(mockBookRepository.findBookById(eq(bookId))).thenThrow(new BookNotFoundException(message));

    BookNotFoundException e =
        assertThrows(BookNotFoundException.class, () -> bookService.findById(bookId));
    assertEquals(message, e.getMessage());

    verify(mockBookRepository, times(1)).findBookById(eq(bookId));
    verify(mockBookRepository, never()).getBookOwner(eq(userId));
  }

  @Test
  @DisplayName("when delete book by id, then delete this book")
  void whenDeleteBookByIdThenDeleteBook() {
    final int bookId = 1;

    when(mockBookRepository.existsById(eq(bookId))).thenReturn(true);
    doNothing().when(mockBookRepository).deleteById(eq(bookId));

    assertDoesNotThrow(() -> bookService.deleteById(bookId));

    verify(mockBookRepository, times(1)).existsById(bookId);
    verify(mockBookRepository, times(1)).deleteById(eq(bookId));
  }

  @Test
  @DisplayName("when delete non-existing book, then throw BookNotFoundException")
  void whenDeleteNonExistingBookThenTrowBookNotFoundException() {
    final int bookId = 1;
    final String message = String.format("Book with id [%d] not found.", bookId);

    when(mockBookRepository.existsById(eq(bookId))).thenThrow(new BookNotFoundException(message));

    BookNotFoundException e =
        assertThrows(BookNotFoundException.class, () -> bookService.deleteById(bookId));
    assertEquals(message, e.getMessage());

    verify(mockBookRepository, times(1)).existsById(bookId);
    verify(mockBookRepository, never()).deleteById(eq(bookId));
  }

  private static User generateUser(int userId) {
    return User.builder().id(userId).email("email@gmail.com").build();
  }

  private static Book generateValidBook(User user) {
    return Book.builder()
        .id(1)
        .title("In the shadow")
        .author("F. Johnson")
        .genre("fiction")
        .description("Description...")
        .publicationYear(Year.of(1900))
        .language("eng")
        .exchangeFormat(BookExchangeFormat.GIFT)
        .owner(user)
        .build();
  }

  private static BookSaveDTO generateValidBookSaveDto() {
    return BookSaveDTO.builder()
        .title("In the shadow")
        .author("F. Johnson")
        .genre("fiction")
        .publicationYear(Year.of(1900))
        .exchangeFormat(BookExchangeFormat.GIFT.getFormat())
        .language("eng")
        .description("Description...")
        .build();
  }
}
