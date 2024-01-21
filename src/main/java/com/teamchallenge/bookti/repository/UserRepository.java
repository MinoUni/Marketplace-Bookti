package com.teamchallenge.bookti.repository;


import com.teamchallenge.bookti.model.UserEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * UserEntity JPA Repository.
 *
 * @author Katherine Sokol
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

  Optional<UserEntity> findByEmail(String email);

  Optional<UserEntity> findById(UUID id);

  boolean existsUserByEmail(String email);

}
