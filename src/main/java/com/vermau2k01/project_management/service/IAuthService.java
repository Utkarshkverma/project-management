package com.vermau2k01.project_management.service;

import com.vermau2k01.project_management.request.AuthenticationRequest;
import com.vermau2k01.project_management.request.PasswordResetRequest;
import com.vermau2k01.project_management.request.RegistrationRequest;
import com.vermau2k01.project_management.response.AuthenticationResponse;
import jakarta.mail.MessagingException;

public interface IAuthService {

    void register(RegistrationRequest request) throws MessagingException;
    AuthenticationResponse login(AuthenticationRequest request);
    void activateAccount(String token) throws MessagingException;
    void requestPasswordReset(PasswordResetRequest request) throws MessagingException;
    void resetPassword(String token, String newPassword) throws MessagingException;
}
