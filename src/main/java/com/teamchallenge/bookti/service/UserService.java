package com.teamchallenge.bookti.service;

import com.teamchallenge.bookti.dto.registration.NewUserRegistrationRequest;
import com.teamchallenge.bookti.dto.registration.RegistrationResponse;
import com.teamchallenge.bookti.exception.UserAlreadyExistsException;
import com.teamchallenge.bookti.exception.PasswordIsNotMatchesException;
import com.teamchallenge.bookti.model.UserEntity;


public interface UserService {

    /**
     * Creates a new {@link UserEntity user} and save it into database
     *
     * @param userDetails the {@link NewUserRegistrationRequest} DTO with basic user information
     * @return {@link RegistrationResponse}
     * @throws PasswordIsNotMatchesException if {@link NewUserRegistrationRequest#getPassword() password} not matches with
     *                                       {@link NewUserRegistrationRequest#getConfirmPassword() confirmPassword}
     * @throws UserAlreadyExistsException    if user with provided email already exists
     */
    RegistrationResponse create(NewUserRegistrationRequest userDetails);
}
