package com.teamchallenge.bookti.user;

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
        User user = User
                .builder()
                .fullName("First_name")
                .email("abc@gmail.com")
                .password("Password1")
                .location("city")
                .build();
        entityManager.persist(user);
        entityManager.flush();

        User nonexistentUser = User
                .builder()
                .fullName("First_name")
                .email("ABC@gmail.com")
                .password("Password1")
                .location("city")
                .build();

        assertTrue(userRepository.existsUserByEmail(user.getEmail()));
        assertFalse(userRepository.existsUserByEmail(nonexistentUser.getEmail()));
    }

    @Test
    void testThatUserSavesCorrectly() {
        User user = User
                .builder()
                .fullName("FirstName")
                .email("abc@gmail.com")
                .password("Password1")
                .location("city")
                .build();

        User user1 = User
                .builder()
                .fullName("FirstName")
                .email("abc")
                .password("Password1")
                .location("city")
                .build();

        User userSaved = userRepository.save(user);
        User user1Saved = userRepository.save(user1);
        System.out.println(user1Saved.getEmail());

        assertEquals(user.getEmail(), userSaved.getEmail());
        assertEquals(user1.getEmail(), user1Saved.getEmail());
    }
}