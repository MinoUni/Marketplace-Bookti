package com.teamchallenge.bookti.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserReviewSaveDTO {

    private Integer reviewerId;

//    private String reviewerName;

    private String message;

    private Float rating;

//    private String avatarUrl;

    private Integer ownerId;

}
