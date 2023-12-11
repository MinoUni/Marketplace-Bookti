package com.teamchallenge.bookti.service.impl;

import com.teamchallenge.bookti.dto.registration.NewUserRegistrationRequest;
import com.teamchallenge.bookti.dto.registration.RegistrationResponse;
import com.teamchallenge.bookti.exception.PasswordIsNotMatchesException;
import com.teamchallenge.bookti.exception.UserAlreadyExistsException;
import com.teamchallenge.bookti.model.UserEntity;
import com.teamchallenge.bookti.repository.UserRepository;
import com.teamchallenge.bookti.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public RegistrationResponse create(NewUserRegistrationRequest userDetails) {
        if (!userDetails.getPassword().equals(userDetails.getConfirmPassword())) {
            throw new PasswordIsNotMatchesException("Password is not matches");
        }
        if (userRepository.existsUserByEmail(userDetails.getEmail())) {
            throw new UserAlreadyExistsException(String.format("User with email '%s' already exists", userDetails.getEmail()));
        }
        userDetails.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        UserEntity user = UserEntity.build(userDetails);
        userRepository.save(user);
        return RegistrationResponse.builder()
                .message(String.format("User with email '%s' is created", userDetails.getEmail()))
                .statusCode(HttpStatus.CREATED.value())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
