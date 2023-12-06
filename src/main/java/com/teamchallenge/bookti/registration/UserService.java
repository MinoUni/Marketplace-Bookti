package com.teamchallenge.bookti.registration;

import com.teamchallenge.bookti.exception.PasswordIsNotConfirmedException;
import com.teamchallenge.bookti.exception.UserAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public RegistrationResponse createUser(UserDto userDto) {
        if (!userDto.confirmPassword(userDto.getPassword(), userDto.getConfirmPassword())) {
            throw new PasswordIsNotConfirmedException("Password is not confirmed");
        }
        if (userRepository.existsUserByEmail(userDto.getEmail())) {
            throw new UserAlreadyExistsException(String.format("User with email '%s' already exists", userDto.getEmail()));
        }
        //TODO passwordEncoder
        userRepository.save(User.build(userDto));
        return RegistrationResponse.builder()
                .message(String.format("User with email '%s' is created", userDto.getEmail()))
                .status(HttpStatus.CREATED.value())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
