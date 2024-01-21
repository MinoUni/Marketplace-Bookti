package com.teamchallenge.bookti.service;

import com.teamchallenge.bookti.dto.user.UserInfo;

/**
 * Interface that contains methods to work with emails.
 *
 * @author Katherine Sokol
 */
public interface EmailService {

  void sendResetPasswordEmail(String token, UserInfo user);
}
