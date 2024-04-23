package com.teamchallenge.bookti.config;

import java.util.List;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Class with application properties.
 *
 * @author Maksym Reva
 */
@Getter
@ConfigurationProperties(prefix = "application")
class ApplicationProperties {

  private final String[] permitAllReq;

  public ApplicationProperties(List<String> permitAllReq) {
    this.permitAllReq = permitAllReq.toArray(String[]::new);
  }
}
