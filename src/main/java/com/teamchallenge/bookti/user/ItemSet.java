package com.teamchallenge.bookti.user;

import java.util.Set;
import lombok.Getter;

/**
 * Container to save items and count collection size.
 *
 * @author MinoUni
 * @version 1.0
 * @param <T> object type
 */
@Getter
public class ItemSet<T> {

  private final int size;

  private final Set<T> items;

  public ItemSet(Set<T> items) {
    this.size = items.size();
    this.items = items;
  }
}
