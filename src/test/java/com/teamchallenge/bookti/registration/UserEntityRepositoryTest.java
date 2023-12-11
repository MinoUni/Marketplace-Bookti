package com.teamchallenge.bookti.registration;

import com.teamchallenge.bookti.model.UserEntity;
import com.teamchallenge.bookti.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserEntityRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testThatUserExistsByEmail() {
        UserEntity userEntity = UserEntity
                .builder()
                .firstName("First_name")
                .lastName("LastName")
                .email("abc@gmail.com")
                .password("Password1")
                .build();
        entityManager.persist(userEntity);
        entityManager.flush();

        UserEntity nonexistentUserEntity = UserEntity
                .builder()
                .firstName("First_name")
                .lastName("LastName")
                .email("ABC@gmail.com")
                .password("Password1")
                .build();

        assertTrue(userRepository.existsUserByEmail(userEntity.getEmail()));
        assertFalse(userRepository.existsUserByEmail(nonexistentUserEntity.getEmail()));
    }

    @Test
    void testThatUserSavesCorrectly() {
        UserEntity userEntity = UserEntity
                .builder()
                .firstName("FirstName")
                .lastName("LastName")
                .email("abc@gmail.com")
                .password("Password1")
                .build();

        UserEntity userEntity1 = UserEntity
                .builder()
                .firstName("FirstName")
                .lastName("LastName")
                .email("abc")
                .password("Password1")
                .build();

        UserEntity userEntitySaved = userRepository.save(userEntity);
        UserEntity userEntity1Saved = userRepository.save(userEntity1);
        System.out.println(userEntity1Saved.getEmail());

        assertEquals(userEntity.getEmail(), userEntitySaved.getEmail());
        assertEquals(userEntity1.getEmail(), userEntity1Saved.getEmail());
    }
}