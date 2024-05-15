package com.teamchallenge.bookti.mapper;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

import com.teamchallenge.bookti.book.Book;
import com.teamchallenge.bookti.book.BookDetailsDTO;
import com.teamchallenge.bookti.book.BookSaveDTO;
import com.teamchallenge.bookti.book.BookUpdateReq;
import com.teamchallenge.bookti.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(
    componentModel = "spring",
    injectionStrategy = CONSTRUCTOR,
    nullValuePropertyMappingStrategy = IGNORE)
public interface BookMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "imageName", ignore = true)
  @Mapping(target = "imageUrl", ignore = true)
  @Mapping(target = "owner", ignore = true)
  void mapBookUpdateToBook(BookUpdateReq bookUpdate, @MappingTarget Book book);

  @Mapping(target = "id", ignore = true)
  Book mapBookSaveDtoAndUserToBook(BookSaveDTO bookSaveDTO, User user, String imageUrl, String imageName);

  BookDetailsDTO mapBookToBookDetailsDTO(Book book);
}
