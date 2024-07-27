package com.teamchallenge.bookti.mapper;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

import com.teamchallenge.bookti.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    injectionStrategy = CONSTRUCTOR,
    nullValuePropertyMappingStrategy = IGNORE)
public interface AuthorizedMapper {

  @Mapping(source = "email", target = "email")
  @Mapping(source = "fullName", target = "fullName")
  @Mapping(source = "avatarUrl", target = "avatarUrl")
  @Mapping(source = "location", target = "location")
  @Mapping(source = "password", target = "password")
  @Mapping(target = "role", constant = "ROLE_USER")
  @Mapping(source = "socialIdentifier", target = "socialIdentifier")
  User toUser(
      String email,
      String fullName,
      String avatarUrl,
      String password,
      String location,
      String socialIdentifier);
}
