package com.teamchallenge.bookti.user;

import com.teamchallenge.bookti.user.dto.UserProfileDTO;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * UserEntity JPA Repository.
 *
 * @author Katherine Sokol
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

  Optional<User> findByEmail(String email);

  Optional<User> findById(Integer id);

  boolean existsUserByEmail(String email);

  @Query(
      """
        select new com.teamchallenge.bookti.user.dto.UserProfileDTO(
          u.id, u.email, u.fullName, u.telegramId, u.creationDate,
          u.location, u.avatarUrl, u.displayEmail, u.displayTelegram)
        from User u
        where u.id = :id
      """)
  Optional<UserProfileDTO> findUserFullInfoById(@Param("id") Integer id);
}
