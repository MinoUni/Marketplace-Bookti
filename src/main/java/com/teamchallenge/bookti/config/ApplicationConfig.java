package com.teamchallenge.bookti.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Application configuration class.
 *
 * @author MinoUni
 */
@Configuration
class ApplicationConfig {

  @Value("${cloudinary.cloud-name}")
  private String cloudName;

  @Value("${cloudinary.api-key}")
  private String apiKey;

  @Value("${cloudinary.api-secret}")
  private String apiSecret;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(10);
  }

  /**
   * Configure connection with Cloudinary workspace.
   *
   * @return {@link Cloudinary}
   */
  @Bean
  public Cloudinary cloudinary() {
    return new Cloudinary(
        ObjectUtils.asMap(
            "cloud_name", cloudName,
            "api_key", apiKey,
            "api_secret", apiSecret,
            "secure", true));
  }
}
