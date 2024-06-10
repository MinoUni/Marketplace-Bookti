package com.teamchallenge.bookti.subscription;

import com.teamchallenge.bookti.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class SubscriptionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    private User user;
    private User subscriber;
    private User secondSubscriber;
    private Subscription subscription;
    private Subscription secondSubscription;

    @BeforeEach
    void createObjects() {
        user = User
                .builder()
                .fullName("Saria Darkoff")
                .email("abc@gmail.com")
                .rating(BigDecimal.valueOf(3.5D))
                .password("Password1")
                .location("city")
                .build();

        subscriber = User
                .builder()
                .fullName("Peter Berger")
                .email("abcdd@gmail.com")
                .rating(BigDecimal.valueOf(3.2D))
                .password("Password1")
                .location("city")
                .build();

        secondSubscriber = User
                .builder()
                .fullName("Amiya Shiro")
                .email("abcdda@gmail.com")
                .rating(BigDecimal.valueOf(3.2D))
                .password("Password1")
                .location("city")
                .build();

        entityManager.persist(user);
        entityManager.persist(subscriber);
        entityManager.persist(secondSubscriber);

        subscription = Subscription
                .builder()
                .user(user)
                .subscriber(subscriber)
                .status(SubscriptionStatus.SUBSCRIBED)
                .build();

        secondSubscription = Subscription
                .builder()
                .user(user)
                .subscriber(secondSubscriber)
                .status(SubscriptionStatus.SUBSCRIBED)
                .build();
    }

    @Test
    @DisplayName("Test SubscriptionRepository method findAllUserSubscriptionById")
    void testMethodFindAllUserSubscriptionById() {
        entityManager.persist(subscription);
        entityManager.persist(secondSubscription);

        List<Subscription> subscriptionList = subscriptionRepository.findAllUserSubscriptionById(user.getId());

        assertAll(
                () -> assertNotNull(subscriptionList),
                () -> assertFalse(subscriptionList.isEmpty()),
                () -> assertEquals(2, subscriptionList.size()),
                () -> assertTrue(subscriptionList.contains(subscription))
        );
    }

    @Test
    @DisplayName("Test SubscriptionRepository method save")
    void testMethodSave() {
        subscriptionRepository.save(subscription);

        Subscription savedSubscription = entityManager.find(Subscription.class, subscription.getId());

        assertEquals(subscription.getId(), savedSubscription.getId());
        assertEquals(subscription.getUser().getId(), savedSubscription.getUser().getId());
    }

    @Test
    @DisplayName("Test SubscriptionRepository method checkIfUserIsSubscribed")
    void testMethodCheckIfUserIsSubscribed() {
        Integer subscriberId = subscriber.getId();
        entityManager.persist(subscription);

        Optional<Subscription> userIsSubscribed = subscriptionRepository.checkIfUserIsSubscribed(user.getId(), subscriberId);

        assertTrue(userIsSubscribed.isPresent());

        Optional<Subscription> userIsNotSubscribed = subscriptionRepository.checkIfUserIsSubscribed(user.getId(), subscriberId + 2);

        assertFalse(userIsNotSubscribed.isPresent());
    }
}