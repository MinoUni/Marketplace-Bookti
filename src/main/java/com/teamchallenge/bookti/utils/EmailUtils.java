package com.teamchallenge.bookti.utils;

import com.teamchallenge.bookti.user.UserInfo;
import java.text.MessageFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * Email Service.
 *
 * @author Katherine Sokol
 */
@RequiredArgsConstructor
@Component
public class EmailUtils {

  private String mailAddress;

  private final JavaMailSender mailSender;

  public void sendResetPasswordEmail(String token, UserInfo userInfo) {
    mailSender.send(constructResetTokenEmail(token, userInfo));
  }

  private SimpleMailMessage constructResetTokenEmail(String token, UserInfo user) {
    //TODO update message with locale
    String domainName = "Bookti";
    String url = domainName + "/renamePassword?resetToken=" + token;
    String message = MessageFormat.format("""
            Dear {0}!
            You have received this email to change your forgotten password on {1}.
            Follow this link to continue: {2}
            """,
        user.getFullName(), domainName, url);
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
