package com.teamchallenge.bookti.service.impl;

import com.teamchallenge.bookti.dto.user.UserInfo;
import com.teamchallenge.bookti.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.text.MessageFormat;

@RequiredArgsConstructor
@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    @Value("${gmail-username}")
    private String mailAddress;

    @Value("${frontend-url}")
    private String clientDomain;

    @Override
    public SimpleMailMessage constructResetTokenEmail(String contextPath, String token, UserInfo user) {
        //TODO update message with locale
        String url = contextPath + "?token=" + token;
        String message = MessageFormat.format("Dear {0} {1}!\nYou have received this email to change your forgotten password on {2}. Follow this link to continue: {3}",
                user.getFirstName(), user.getLastName(), clientDomain, url);
        return constructEmail("Reset Password", message, user);
    }

    private SimpleMailMessage constructEmail(String subject, String body, UserInfo user) {
        final SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText(body);
        email.setTo(user.getEmail());
        email.setFrom(mailAddress);
        return email;
    }
}
