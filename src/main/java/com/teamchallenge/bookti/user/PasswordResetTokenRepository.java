package com.teamchallenge.bookti.user;

import jakarta.transaction.Transactional;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * PasswordResetToken JPA Repository.
 *
 * @author Katherine Sokol
 */
interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

  Optional<PasswordResetToken> findByToken(String token);

  PasswordResetToken findByUser(UserEntity user);

  @Modifying
  @Transactional
  @Query("delete from PasswordResetToken t where t.user = :user")
  void deletePasswordResetTokenByUserId(@Param("user") UserEntity user);
}