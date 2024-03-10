package com.teamchallenge.bookti.config;

import com.teamchallenge.bookti.security.CustomUserDetailsService;
import com.teamchallenge.bookti.security.handler.CustomAccessDeniedHandler;
import com.teamchallenge.bookti.security.handler.CustomRestAuthenticationEntryPoint;
import com.teamchallenge.bookti.security.jwt.JwtToAuthorizedUserConverter;
import com.teamchallenge.bookti.security.oauth2.handler.FailureHandler;
import com.teamchallenge.bookti.security.oauth2.handler.SuccessHandler;
import com.teamchallenge.bookti.security.oauth2.repository.CustomAuthorizationRequestRepository;
import com.teamchallenge.bookti.security.oauth2.service.CustomOauth2UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
  private final SuccessHandler successHandler;
  private final FailureHandler failureHandler;
  private final CustomOauth2UserService userService;
  private final CustomAuthorizationRequestRepository authorizationRequestRepository;

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
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session
              .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .cors(cors -> cors
              .configurationSource(corsConfigurationSource())
            )
            .authenticationProvider(authenticationProvider())
            .exceptionHandling(exception -> exception
              .authenticationEntryPoint(restAuthenticationEntryPoint)
              .accessDeniedHandler(accessDeniedHandler)
            )
            .authorizeHttpRequests(authorize -> authorize
              .requestMatchers(HttpMethod.GET, "/api/v1/books", "/api/v1/books/{id}")
                .permitAll()
              .requestMatchers(applicationProperties.getPermitAllReq()).permitAll()
              .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
              .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtToUserConverter))
            )
            .oauth2Login(oauth2 -> oauth2
              .authorizationEndpoint(authEndpoint -> authEndpoint
                .baseUri("/oauth2/authorize")
                .authorizationRequestRepository(authorizationRequestRepository)
              )
              .redirectionEndpoint(redirectEndpoint -> redirectEndpoint
                .baseUri("/oauth2/callback/*")
              )
              .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                .userService(userService)
              )
              .successHandler(successHandler)
              .failureHandler(failureHandler)
            )
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
    cors.setAllowedOrigins(applicationProperties.getAllowedOrigins());
    cors.setAllowedMethods(List.of("GET", "POST", "DELETE", "PATCH", "PUT", "OPTIONS"));
    cors.setAllowedHeaders(
        List.of("X-Requested-With", "Origin", "Content-Type", "Accept", "Authorization"));
    cors.setExposedHeaders(
        List.of(
            "Content-Type",
            "Cache-Control",
            "Content-Language",
            "Content-Length",
            "Last-Modified"));
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", cors);
    return source;
  }
}
