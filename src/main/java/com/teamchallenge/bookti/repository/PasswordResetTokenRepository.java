package com.teamchallenge.bookti.repository;

import com.teamchallenge.bookti.model.PasswordResetToken;
import com.teamchallenge.bookti.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    PasswordResetToken findByUser(UserEntity user);
    @Query("delete from PasswordResetToken t where t.user = ?1")
    void deletePasswordResetTokenByUserId(UserEntity user);
}