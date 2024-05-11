package com.teamchallenge.bookti.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserReviewRepository extends JpaRepository<UserReview, Integer>  {

    @Query("SELECT AVG(ur.rating) From UserReview ur WHERE ur.owner.id =:ownerId")
    Optional<Float> getUserRating(@Param("ownerId") Integer ownerId);

}
