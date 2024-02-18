package com.teamchallenge.bookti.model;

import static jakarta.persistence.FetchType.LAZY;

import com.teamchallenge.bookti.dto.book.BookProfile;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;

/**
 * Class that describes Book entity.
 *
 * @author MinoUni
 * @version 1.0
 */
@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "books")
public class Book {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column
  private String title;

  @Column
  private String author;

  @Column
  private String genre;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(name = "image_name")
  private String imageName;

  @Column(name = "image_url")
  private String imageUrl;

  @Column
  private String language;

  @Column(name = "publication_date", columnDefinition = "DATE")
  private LocalDate publicationDate;

  @Column(name = "trade_format")
  private String tradeFormat;

  @ToString.Exclude
  @ManyToOne(fetch = LAZY, optional = false)
  private UserEntity owner;

  // todo: Add 'comments' relationship 1:n

  /**
   * Method to build book entity from book DTO.
   *
   * @param bookProfile book DTO
   * @param imageUrl image url
   * @param user user entity
   * @return book entity
   */
  public static Book build(BookProfile bookProfile,
                           String imageUrl,
                           String imageName,
                           UserEntity user) {
    return Book.builder()
        .title(bookProfile.getTitle())
        .author(bookProfile.getAuthor())
        .genre(bookProfile.getGenre())
        .description(bookProfile.getDescription())
        .imageName(imageName)
        .imageUrl(imageUrl)
        .language(bookProfile.getLanguage())
        .publicationDate(bookProfile.getPublicationDate())
        .tradeFormat(bookProfile.getTradeFormat())
        .owner(user)
        .build();
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null) {
      return false;
    }
    Class<?> objEffectiveClass =
        o instanceof HibernateProxy
            ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
            : o.getClass();
    Class<?> thisEffectiveClass =
        this instanceof HibernateProxy
            ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
            : this.getClass();
    if (thisEffectiveClass != objEffectiveClass) {
      return false;
    }
    Book book = (Book) o;
    return getId() != null && Objects.equals(getId(), book.getId());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy
        ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
        : getClass().hashCode();
  }
}
