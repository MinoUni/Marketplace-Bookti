package com.teamchallenge.bookti.service;

import com.teamchallenge.bookti.dto.authorization.NewUserRegistrationRequest;
import com.teamchallenge.bookti.model.PasswordResetToken;
import com.teamchallenge.bookti.dto.user.UserInfo;
import com.teamchallenge.bookti.exception.PasswordIsNotMatchesException;
import com.teamchallenge.bookti.exception.UserAlreadyExistsException;
import com.teamchallenge.bookti.exception.UserNotFoundException;
import com.teamchallenge.bookti.model.UserEntity;
import com.teamchallenge.bookti.security.AuthorizedUser;

import java.util.UUID;


public interface UserService {

    /**
     * Creates a new {@link UserEntity user} and save it into database
     *
     * @param userDetails the {@link NewUserRegistrationRequest} DTO with basic user information
     * @return {@link AuthorizedUser}
     * @throws PasswordIsNotMatchesException if {@link NewUserRegistrationRequest#getPassword() password} not matches with
     *                                       {@link NewUserRegistrationRequest#getConfirmPassword() confirmPassword}
     * @throws UserAlreadyExistsException    if user with provided email already exists
     */
    AuthorizedUser create(NewUserRegistrationRequest userDetails);

    /**
     * Return information about user
     *
     * @param id user uuid
     * @return {@link UserInfo user} DTO
     * @throws UserNotFoundException if user with provided id not found
     */
    UserInfo findById(UUID id);

    /**
     * Return information about user
     *
     * @param email user uuid
     * @return {@link UserInfo user} DTO
     * @throws UserNotFoundException if user with provided id not found
     */
    UserInfo findUserByEmail (String email);

    public void changeUserPassword(UUID userId, String newPassword);

    PasswordResetToken createPasswordResetTokenForUser(UserInfo user, String token);

    PasswordResetToken getPasswordResetToken(String token);

    UserInfo getUserByPasswordResetToken(String passwordResetToken);
}
