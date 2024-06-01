package com.teamchallenge.bookti.review;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserReviewRepository extends JpaRepository<UserReview, Integer> {

  @Query("SELECT AVG(ur.rating) From UserReview ur WHERE ur.owner.id =:ownerId")
  Optional<BigDecimal> getUserRating(@Param("ownerId") Integer ownerId);

  @Query(
      """
        select ur
        from UserReview ur
        where ur.owner.id = :id ORDER BY ur.creationDate DESC
      """)
  List<UserReview> findAllUserReceivedReviewsById(@Param("id") Integer id);

  @Query(
      """
        select ur
        from UserReview ur
        where ur.reviewers.id = :id ORDER BY ur.creationDate DESC
      """)
  List<UserReview> findAllUserLeftReviewById(@Param("id") Integer id);
}
