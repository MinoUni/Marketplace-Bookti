package com.teamchallenge.bookti.model;

import com.teamchallenge.bookti.exception.PasswordResetTokenIsExpiredException;
import com.teamchallenge.bookti.exception.PasswordResetTokenNotFoundException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Calendar;
import java.util.Date;
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PasswordResetToken {
    private static final int EXPIRATION = 60;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String token;

    @OneToOne(targetEntity = UserEntity.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private UserEntity user;

    private Date expiryDate;

    public PasswordResetToken(UserEntity user, String token) {
        this.token = token;
        this.user = user;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }

    private Date calculateExpiryDate(final int expiryTimeInMinutes) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(new Date().getTime());
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }

    public void validate(PasswordResetToken passwordResetToken) {
        if (!isTokenFound(passwordResetToken)) {
            throw new PasswordResetTokenNotFoundException("PasswordResetToken not found");
        }
        if (isTokenExpired(passwordResetToken)) {
            throw new PasswordResetTokenIsExpiredException("PasswordResetToken is Expired");
        }
    }

    private boolean isTokenFound(PasswordResetToken passwordResetToken) {
        return passwordResetToken != null;
    }

    private boolean isTokenExpired(PasswordResetToken passwordResetToken) {
        final Calendar cal = Calendar.getInstance();
        return passwordResetToken.getExpiryDate().before(cal.getTime());
    }
}
