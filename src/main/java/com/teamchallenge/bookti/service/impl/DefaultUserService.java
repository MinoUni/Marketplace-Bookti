package com.teamchallenge.bookti.service.impl;

import com.teamchallenge.bookti.dto.authorization.NewUserRegistrationRequest;
import com.teamchallenge.bookti.dto.authorization.PasswordResetToken;
import com.teamchallenge.bookti.dto.user.UserInfo;
import com.teamchallenge.bookti.exception.PasswordIsNotMatchesException;
import com.teamchallenge.bookti.exception.UserAlreadyExistsException;
import com.teamchallenge.bookti.exception.UserNotFoundException;
import com.teamchallenge.bookti.mapper.AuthorizedUserMapper;
import com.teamchallenge.bookti.model.UserEntity;
import com.teamchallenge.bookti.repository.PasswordResetTokenRepository;
import com.teamchallenge.bookti.repository.UserRepository;
import com.teamchallenge.bookti.security.AuthorizedUser;
import com.teamchallenge.bookti.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository passwordTokenRepository;

    @Override
    public AuthorizedUser create(NewUserRegistrationRequest userDetails) {
        if (!userDetails.getPassword().equals(userDetails.getConfirmPassword())) {
            throw new PasswordIsNotMatchesException("Password is not matches");
        }
        if (userRepository.existsUserByEmail(userDetails.getEmail())) {
            throw new UserAlreadyExistsException(MessageFormat.format("User with email <{0}> already exists", userDetails.getEmail()));
        }
        userDetails.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        UserEntity user = UserEntity.build(userDetails);
        userRepository.save(user);
        return AuthorizedUserMapper.mapFrom(user);
    }

    @Override
    public UserInfo findById(UUID id) {
        var user = userRepository.findById(id)
                    .orElseThrow(() -> new UserNotFoundException(MessageFormat.format("User with id <{0}> not found.", id)));
        return UserInfo.mapFrom(user);
    }

    @Override
    public UserEntity findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(MessageFormat.format("User with email <{0}> not found.", email)));
    }

    @Override
    public void createPasswordResetTokenForUser(UserEntity user, String token) {
        PasswordResetToken passwordResetToken = new PasswordResetToken(user, token);
        passwordTokenRepository.save(passwordResetToken);
    }

    @Override
    public PasswordResetToken getPasswordResetToken(String token) {
        return passwordTokenRepository.findByToken(token);
    }

    @Override
    public Optional<UserEntity> getUserByPasswordResetToken(String token) {
        return Optional.ofNullable(passwordTokenRepository.findByToken(token) .getUser());
    }
}
