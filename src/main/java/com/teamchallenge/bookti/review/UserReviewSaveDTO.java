package com.teamchallenge.bookti.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserReviewSaveDTO {

    @NotBlank(message = "Field <message> must not be blank")
    private String message;

    @NotNull(message = "Field <rating> must not be null")
    @Min(value = 1, message = "Min <rating> value mast be 1")
    @Max(value = 5,message= "Max <rating> value mast be 5")
    private BigDecimal rating;

    @NotNull(message = "Field <ownerId> must not be null")
    @Min(value = 1, message = "Min <ownerId> value mast be 1")
    private Integer ownerId;

}
