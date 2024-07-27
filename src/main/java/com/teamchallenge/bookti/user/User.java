package com.teamchallenge.bookti.user;

import com.teamchallenge.bookti.book.Book;
import com.teamchallenge.bookti.review.UserReview;
import com.teamchallenge.bookti.security.Role;
import com.teamchallenge.bookti.subscription.Subscription;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;

/**
 * UserEntity is Entity Class witch contains information about user from database.
 *
 * @author Maksym Reva
 */
@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "full_name", nullable = false)
  private String fullName;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String password;

  @Builder.Default
  @Column(name = "creation_date", columnDefinition = "DATE")
  private LocalDate creationDate = LocalDate.now();

  @Column(name = "avatar_name")
  private String avatarName;

  @Column(name = "avatar_url")
  private String avatarUrl;

  @Column(nullable = false)
  private String location;

  @Builder.Default
  @Column(columnDefinition = "DECIMAL(10,1) DEFAULT 0.0")
  private BigDecimal rating = BigDecimal.ZERO;

  @Column(name = "telegram_id", length = 32)
  private String telegramId;

  @Column
  @Enumerated(EnumType.STRING)
  private Role role;

  @Column
  private String socialIdentifier;

  @Builder.Default
  @Column(name = "display_email")
  private Boolean displayEmail = Boolean.FALSE;

  @Builder.Default
  @Column(name = "display_telegram")
  private Boolean displayTelegram = Boolean.FALSE;

  @ToString.Exclude
  @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private Set<UserReview> leftReviews;

  @ToString.Exclude
  @OneToMany(mappedBy = "reviewers", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private Set<UserReview> receivedReviews;

  @ToString.Exclude
  @OneToMany(mappedBy = "owner")
  private Set<Book> books;

  @ToString.Exclude
  @ManyToMany
  @JoinTable(
      name = "users_books",
      joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
      inverseJoinColumns = {@JoinColumn(name = "book_id", referencedColumnName = "id")})
  private Set<Book> wishlist;

  @ToString.Exclude
  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private Set<Subscription> subscriptions;

  @ToString.Exclude
  @OneToMany(mappedBy = "subscriber", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private Set<Subscription> subscriber;

  @Override
  public final boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    Class<?> objEffectiveClass =
        obj instanceof HibernateProxy
            ? ((HibernateProxy) obj).getHibernateLazyInitializer().getPersistentClass()
            : obj.getClass();
    Class<?> thisEffectiveClass =
        this instanceof HibernateProxy
            ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
            : this.getClass();
    if (thisEffectiveClass != objEffectiveClass) {
      return false;
    }
    User that = (User) obj;
    return getId() != null && Objects.equals(getId(), that.getId());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy
        ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
        : getClass().hashCode();
  }
}
