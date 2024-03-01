package com.teamchallenge.bookti.user;

import com.teamchallenge.bookti.exception.PasswordIsNotMatchesException;
import com.teamchallenge.bookti.exception.PasswordResetTokenNotFoundException;
import com.teamchallenge.bookti.exception.UserAlreadyExistsException;
import com.teamchallenge.bookti.exception.UserNotFoundException;
import com.teamchallenge.bookti.utils.AuthorizedUserMapper;
import com.teamchallenge.bookti.security.AuthorizedUser;

import java.text.MessageFormat;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.teamchallenge.bookti.user.Role.ROLE_USER;

/**
 * User service.
 *
 * @author Katherine Sokol and Maksym Reva
 */
@RequiredArgsConstructor
@Service
class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final PasswordResetTokenRepository passwordTokenRepository;

  /**
   * Creates a new {@link UserEntity user} and save it into database.
   *
   * @param userDetails the {@link NewUserRegistrationRequest} DTO with basic user information
   * @return {@link AuthorizedUser}
   * @throws PasswordIsNotMatchesException if
   *         {@link NewUserRegistrationRequest#getPassword() password} not matches with
   *         {@link NewUserRegistrationRequest#getConfirmPassword() confirmPassword}
   * @throws UserAlreadyExistsException    if user with provided email already exists
   */
  public AuthorizedUser create(NewUserRegistrationRequest userDetails) {
    if (!userDetails.getPassword().equals(userDetails.getConfirmPassword())) {
      throw new PasswordIsNotMatchesException("Password is not matches");
    }
    if (userRepository.existsUserByEmail(userDetails.getEmail())) {
      throw new UserAlreadyExistsException(
          MessageFormat.format("User with email <{0}> already exists", userDetails.getEmail()));
    }
    userDetails.setPassword(passwordEncoder.encode(userDetails.getPassword()));
    UserEntity user = build(userDetails);
    userRepository.save(user);
    return AuthorizedUserMapper.mapFrom(user);
  }

  /**
   * Return information about user.
   *
   * @param id user uuid
   * @return {@link UserInfo user} DTO
   * @throws UserNotFoundException if user with provided id not found
   */
  public UserInfo findById(UUID id) {
    var user = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(
            MessageFormat.format("User with id <{0}> not found.", id)));
    return UserInfo.mapFrom(user);
  }

  /**
   * Return information about user.
   *
   * @param email user uuid
   * @return {@link UserInfo user} DTO
   * @throws UserNotFoundException if user with provided id not found
   */
  public UserInfo findUserByEmail(String email) {
    UserEntity user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UserNotFoundException(
            MessageFormat.format("User with email <{0}> not found.", email)));
    return UserInfo.mapFrom(user);
  }

  public void changeUserPassword(UUID userId, String newPassword) {
    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(
            MessageFormat.format("User with id <{0}> not found.", userId)));
    user.setPassword(passwordEncoder.encode(newPassword));
    userRepository.save(user);
    passwordTokenRepository.deletePasswordResetTokenByUserId(user);
  }

  public PasswordResetToken createPasswordResetTokenForUser(UserInfo user, String token) {
    UserEntity userEntity = userRepository.findById(user.getId())
        .orElseThrow(() -> new UserNotFoundException(
            MessageFormat.format("User with id <{0}> not found.", user.getId())));
    if (passwordTokenRepository.findByUser(userEntity) != null) {
      passwordTokenRepository.deletePasswordResetTokenByUserId(userEntity);
    }
    PasswordResetToken passwordResetToken = new PasswordResetToken(userEntity, token);
    return passwordTokenRepository.save(passwordResetToken);
  }

  public PasswordResetToken getPasswordResetToken(String token) {
    return passwordTokenRepository.findByToken(token)
        .orElseThrow(() -> new PasswordResetTokenNotFoundException(
            MessageFormat.format("Password reset token <{0}> not found.", token)));
  }

  public UserInfo getUserByPasswordResetToken(String passwordResetToken) {
    UserEntity user = getPasswordResetToken(passwordResetToken).getUser();
    return UserInfo.mapFrom(user);
  }

  /**
   * Builds {@link UserEntity} from {@link NewUserRegistrationRequest}.
   *
   * @param userDetails {@link NewUserRegistrationRequest}
   * @return {@link UserEntity}
   */
  private UserEntity build(NewUserRegistrationRequest userDetails) {
    return UserEntity.builder()
            .fullName(userDetails.getFullName())
            .email(userDetails.getEmail())
            .password(userDetails.getPassword())
            .city(userDetails.getCity())
            .role(ROLE_USER)
            .build();
  }
}
