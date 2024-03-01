package com.teamchallenge.bookti.user;

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
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;

/**
 * Entity class that contains information about password reset token in database.
 *
 * @author Katherine Sokol
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
class PasswordResetToken {
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
    this.expiryDate = calculateExpiryDate();
  }

  private Date calculateExpiryDate() {
    final Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(new Date().getTime());
    cal.add(Calendar.MINUTE, PasswordResetToken.EXPIRATION);
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

  @Override
  public final boolean equals(Object o) {
    if (this == o) return true;
    if (o == null) return false;
    Class<?> oEffectiveClass =
        o instanceof HibernateProxy
            ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
            : o.getClass();
    Class<?> thisEffectiveClass =
        this instanceof HibernateProxy
            ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
            : this.getClass();
    if (thisEffectiveClass != oEffectiveClass) return false;
    PasswordResetToken that = (PasswordResetToken) o;
    return getId() != null && Objects.equals(getId(), that.getId());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy
        ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
        : getClass().hashCode();
  }
}
