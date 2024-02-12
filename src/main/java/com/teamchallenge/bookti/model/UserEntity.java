package com.teamchallenge.bookti.model;

import static com.teamchallenge.bookti.model.Role.ROLE_USER;

import com.teamchallenge.bookti.dto.authorization.NewUserRegistrationRequest;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
public class UserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "full_name", nullable = false)
  private String fullName;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(name = "avatar_url")
  private String avatarUrl;

  @Column
  @Enumerated(EnumType.STRING)
  private Role role;

  @ToString.Exclude
  @OneToMany(mappedBy = "owner")
  private Set<Book> books;

  /**
   * Builds {@link UserEntity} from {@link NewUserRegistrationRequest}.
   *
   * @param userDetails {@link NewUserRegistrationRequest}
   * @return {@link UserEntity}
   */
  public static UserEntity build(NewUserRegistrationRequest userDetails) {
    return UserEntity.builder()
        .fullName(userDetails.getFullName())
        .email(userDetails.getEmail())
        .password(userDetails.getPassword())
        .role(ROLE_USER)
        .build();
  }

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
    UserEntity that = (UserEntity) obj;
    return getId() != null && Objects.equals(getId(), that.getId());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy
        ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
        : getClass().hashCode();
  }
}
