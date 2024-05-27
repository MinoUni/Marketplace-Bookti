package com.teamchallenge.bookti.book;

import static jakarta.persistence.FetchType.LAZY;

import com.teamchallenge.bookti.converter.BookExchangeFormatConverter;
import com.teamchallenge.bookti.converter.BookStatusConverter;
import com.teamchallenge.bookti.converter.YearConverter;
import com.teamchallenge.bookti.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Year;
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
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

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

  @Convert(converter = YearConverter.class)
  @Column(name = "publication_year", columnDefinition = "smallint")
  private Year publicationYear;

  @Convert(converter = BookExchangeFormatConverter.class)
  @Column(name = "exchange_format")
  private BookExchangeFormat exchangeFormat;

  @Builder.Default
  @Convert(converter = BookStatusConverter.class)
  @Column(name = "status")
  private BookStatus status = BookStatus.PENDING_APPROVAL;

  @ToString.Exclude
  @ManyToOne(fetch = LAZY, optional = false)
  private User owner;

  @ToString.Exclude
  @ManyToMany(mappedBy = "wishlist")
  private Set<User> candidates;

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
