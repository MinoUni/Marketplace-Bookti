package com.teamchallenge.bookti.mapper;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

import com.teamchallenge.bookti.user.User;
import com.teamchallenge.bookti.user.dto.UserUpdateDto;
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

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "avatarName", ignore = true)
  @Mapping(target = "avatarUrl", ignore = true)
  @Mapping(target = "books", ignore = true)
  void mapUserUpdateToUser(UserUpdateDto userUpdate, @MappingTarget User user);
}
