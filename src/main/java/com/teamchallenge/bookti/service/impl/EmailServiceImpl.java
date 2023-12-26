package com.teamchallenge.bookti.service.impl;

import com.teamchallenge.bookti.config.ApplicationProperties;
import com.teamchallenge.bookti.dto.user.UserInfo;
import com.teamchallenge.bookti.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import java.text.MessageFormat;

@RequiredArgsConstructor
@Service
public class EmailServiceImpl implements EmailService {

    @Value("${gmail-username:javadoubts}")
    private String mailAddress;

    private final ApplicationProperties applicationProperties;

    @Override
    public SimpleMailMessage constructResetTokenEmail(String contextPath, String token, UserInfo user) {
        //TODO update message with locale
        String url = contextPath + "?token=" + token;
        String message = MessageFormat.format("Dear {0}!\nYou have received this email to change your forgotten password on {1}. Follow this link to continue: {2}",
                user.getFullName(), applicationProperties.getAllowedOrigins().get(0), url);
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
