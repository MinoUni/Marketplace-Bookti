package com.teamchallenge.bookti.service;

import com.teamchallenge.bookti.dto.user.UserInfo;

public interface EmailService {

    void sendResetPasswordEmail (String token, UserInfo user);
}
