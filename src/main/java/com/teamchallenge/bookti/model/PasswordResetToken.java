package com.teamchallenge.bookti.model;

import com.teamchallenge.bookti.exception.PasswordResetTokenIsExpiredException;
import com.teamchallenge.bookti.exception.PasswordResetTokenNotFoundException;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.util.Calendar;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class that contains information about password reset token in database.
 *
 * @author Katherine Sokol
 */
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

  /**
   * Constructor that creates {@link PasswordResetToken} for {@link UserEntity user} with given
   * token and calculated expiryDate.
   *
   * @param user {@link UserEntity} for whom {@link PasswordResetToken} will be created
   * @param token String that contains random UUID
   */
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

  /**
   * Checks if {@link PasswordResetToken} is found and not expired.
   *
   * @param passwordResetToken {@link PasswordResetToken} that will be validated
   */
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
