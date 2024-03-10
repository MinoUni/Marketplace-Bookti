package com.teamchallenge.bookti.utils;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

import com.teamchallenge.bookti.book.Book;
import com.teamchallenge.bookti.book.BookUpdateReq;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper to map objects.
 *
 * @author MinoUni
 * @version 1.0
 */
@Mapper(
    componentModel = "spring",
    injectionStrategy = CONSTRUCTOR,
    nullValuePropertyMappingStrategy = IGNORE)
public interface MapperUtils {

  /**
   * The method to map {@link BookUpdateReq} into {@link Book}.
   *
   * @param bookInfo new info about book to save
   * @param book book model
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "imageName", ignore = true)
  @Mapping(target = "imageUrl", ignore = true)
  @Mapping(target = "owner", ignore = true)
  void mapBookUpdateToBook(BookUpdateReq bookInfo, @MappingTarget Book book);
}
