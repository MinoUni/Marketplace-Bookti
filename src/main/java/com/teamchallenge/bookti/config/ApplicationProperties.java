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
public class ApplicationProperties {

  private final String[] permitAllReq;
  private final List<String> allowedOrigins;

  public ApplicationProperties(List<String> permitAllReq, List<String> allowedOrigins) {
    this.permitAllReq = permitAllReq.toArray(String[]::new);
    this.allowedOrigins = allowedOrigins;
  }
}
