package com.vermau2k01.project_management.service;

public interface IEmailService {

    void sendVerificationEmail(String name, String to, String token);
    void sendPasswordResetEmail(String name, String to, String token);
}
