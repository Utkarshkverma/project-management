package com.vermau2k01.project_management.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailService implements IEmailService {

    @Value("${spring.mail.username}")
    private String from;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Override
    @Async
    public void sendVerificationEmail(String name, String to, String token) {
        try {

            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("otp",token);

            String text = templateEngine.process("verification-email", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            helper.setPriority(1);
            helper.setSubject("New User Account Verification");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setText(text, true);
            mailSender.send(message);
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            throw new RuntimeException(exception.getMessage());
        }
    }

    @Override
    public void sendPasswordResetEmail(String name, String to, String token) {
        try{

            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("otp",token);

            String text = templateEngine.process("forgot-password", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            helper.setPriority(1);
            helper.setSubject("Reset Password");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setText(text, true);
            mailSender.send(message);
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            throw new RuntimeException(exception.getMessage());
        }

    }
}
