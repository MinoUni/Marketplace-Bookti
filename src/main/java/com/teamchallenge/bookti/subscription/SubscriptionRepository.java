package com.teamchallenge.bookti.subscription;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription,Integer> {

    @Query("select s from Subscription s where s.user.id = :id")
    List<Subscription> findAllUserSubscriptionById(@Param("id") Integer id);

    @Query("SELECT s FROM Subscription s WHERE s.user.id = :userId AND s.subscriber.id=:subscriId")
    Optional<Subscription> checkIfUserIsSubscribed(@Param("userId") Integer userId, @Param("subscriId") Integer subscriId);

}
