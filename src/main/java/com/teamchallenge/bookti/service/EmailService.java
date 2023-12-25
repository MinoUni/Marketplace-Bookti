package com.teamchallenge.bookti.service;

import com.teamchallenge.bookti.dto.user.UserInfo;
import org.springframework.mail.SimpleMailMessage;

public interface EmailService {

    SimpleMailMessage constructResetTokenEmail(String contextPath, String token, UserInfo user);
}
