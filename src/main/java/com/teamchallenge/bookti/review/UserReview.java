package com.teamchallenge.bookti.review;

import com.teamchallenge.bookti.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Table(name = "UserReview")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "reviewer_name")
    private String reviewerName;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(name = "user_rating")
    private Float rating;

    @Column(name = "creation_date", columnDefinition = "DATE")
    private LocalDate creationDate = LocalDate.now();;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @ToString.Exclude
    @ManyToOne()
    @JoinColumn(name = "userId", nullable = false)
    private User owner;


//    @ManyToOne()
//    @JoinColumn(name = "bookId", nullable = true)
//    private Book book;

}
