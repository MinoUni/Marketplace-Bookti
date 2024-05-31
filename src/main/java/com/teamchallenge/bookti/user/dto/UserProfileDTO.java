package com.teamchallenge.bookti.user.dto;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.teamchallenge.bookti.book.BookProfileDTO;
import com.teamchallenge.bookti.user.User;
import java.math.BigDecimal;
import com.teamchallenge.bookti.utils.ItemSet;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UserProfileDTO {

    private Integer id;

    private String email;

    private String fullName;

    private BigDecimal rating;

    private String telegramId;

    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd")
    private LocalDate creationDate;

    private String location;

    private String avatarUrl;

    private Boolean displayEmail;

    private Boolean displayTelegram;

    private ItemSet<BookProfileDTO> books;

    private ItemSet<BookProfileDTO> wishlist;

    public UserProfileDTO(
            Integer id,
            String email,
            String fullName,
            BigDecimal rating,
            String telegramId,
            LocalDate creationDate,
            String location,
            String avatarUrl,
            Boolean displayEmail,
            Boolean displayTelegram) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.rating = rating;
        this.telegramId = telegramId;
        this.creationDate = creationDate;
        this.location = location;
        this.avatarUrl = avatarUrl;
        this.displayEmail = displayEmail;
        this.displayTelegram = displayTelegram;
    }

    public static UserProfileDTO mapFrom(User user) {
        return UserProfileDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .rating(user.getRating())
                .telegramId(user.getTelegramId())
                .creationDate(user.getCreationDate())
                .location(user.getLocation())
                .avatarUrl(user.getAvatarUrl())
                .displayEmail(user.getDisplayEmail())
                .displayTelegram(user.getDisplayTelegram())
                .build();
    }
}
