package com.teamchallenge.bookti.registration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testThatUserExistsByEmail() {
        //given
        User user = User
                .builder()
                .firstName("First_name")
                .lastName("LastName")
                .email("abc@gmail.com")
                .password("Password1")
                .build();
        entityManager.persist(user);
        entityManager.flush();

        User nonexistentUser = User
                .builder()
                .firstName("First_name")
                .lastName("LastName")
                .email("ABC@gmail.com")
                .password("Password1")
                .build();

        //when
        boolean exists = userRepository.existsUserByEmail(user.getEmail());
        boolean doesNotExist = userRepository.existsUserByEmail(nonexistentUser.getEmail());

        //then
        assertTrue(exists);
        assertFalse(doesNotExist);
    }

    @Test
    void testThatUserSavesCorrectly() {
        //given
        User user = User
                .builder()
                .firstName("FirstName")
                .lastName("LastName")
                .email("abc@gmail.com")
                .password("Password1")
                .build();

        User user1 = User
                .builder()
                .firstName("FirstName")
                .lastName("LastName")
                .email("abc")
                .password("Password1")
                .build();

        //when
        User userSaved = userRepository.save(user);
        User user1Saved = userRepository.save(user1);
        System.out.println(user1Saved.getEmail());

        //then
        assertEquals(user.getEmail(), userSaved.getEmail());
        assertEquals(user1.getEmail(), user1Saved.getEmail());
    }
}