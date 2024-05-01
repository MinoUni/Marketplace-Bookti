package com.teamchallenge.bookti.config;

import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CACHE_CONTROL;
import static org.springframework.http.HttpHeaders.CONTENT_LANGUAGE;
import static org.springframework.http.HttpHeaders.CONTENT_LENGTH;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpHeaders.LAST_MODIFIED;
import static org.springframework.http.HttpHeaders.ORIGIN;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.OPTIONS;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

import com.teamchallenge.bookti.security.CustomUserDetailsService;
import com.teamchallenge.bookti.security.handler.CustomAccessDeniedHandler;
import com.teamchallenge.bookti.security.handler.CustomRestAuthenticationEntryPoint;
import com.teamchallenge.bookti.security.jwt.JwtToAuthorizedUserConverter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Class with security configurations.
 *
 * @author Maksym Reva
 */
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ApplicationProperties.class)
class SecurityConfig {

  private final CustomUserDetailsService customUserDetailsService;
  private final JwtToAuthorizedUserConverter jwtToUserConverter;
  private final CustomRestAuthenticationEntryPoint restAuthenticationEntryPoint;
  private final CustomAccessDeniedHandler accessDeniedHandler;
  private final ApplicationProperties applicationProperties;
  private final PasswordEncoder passwordEncoder;

  @Value("${application.cors.origins}")
  private List<String> allowedOrigins;

  /**
   * Creates {@link SecurityFilterChain} with needed configurations.
   *
   * @param http {@link HttpSecurity}
   * @return {@link SecurityFilterChain}
   * @throws Exception if exception is not handled
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .authenticationProvider(authenticationProvider())
        .exceptionHandling(
            exception ->
                exception
                    .authenticationEntryPoint(restAuthenticationEntryPoint)
                    .accessDeniedHandler(accessDeniedHandler))
        .authorizeHttpRequests(
            authorize ->
                authorize
                    .requestMatchers(GET, "/books", "/books/{id}")
                    .permitAll()
                    .requestMatchers(applicationProperties.getPermitAllReq())
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .oauth2ResourceServer(
            oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtToUserConverter)))
        .build();
  }

  /**
   * Creates {@link AuthenticationProvider}.
   *
   * @return {@link AuthenticationProvider}
   */
  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
    authenticationProvider.setUserDetailsService(customUserDetailsService);
    authenticationProvider.setPasswordEncoder(passwordEncoder);
    return authenticationProvider;
  }

  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  /**
   * Creates {@link CorsConfigurationSource} with allowed origins, methods and headers.
   *
   * @return {@link CorsConfigurationSource}
   */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration cors = new CorsConfiguration();
    cors.setAllowedOrigins(allowedOrigins);
    cors.setAllowedMethods(
        List.of(GET.name(), POST.name(), DELETE.name(), PATCH.name(), PUT.name(), OPTIONS.name()));
    cors.setAllowedHeaders(List.of(ORIGIN, CONTENT_TYPE, ACCEPT, AUTHORIZATION));
    cors.setExposedHeaders(
        List.of(CONTENT_TYPE, CACHE_CONTROL, CONTENT_LANGUAGE, CONTENT_LENGTH, LAST_MODIFIED));
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", cors);
    return source;
  }
}
