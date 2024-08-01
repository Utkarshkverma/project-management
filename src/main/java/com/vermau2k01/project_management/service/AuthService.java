package com.vermau2k01.project_management.service;

import com.vermau2k01.project_management.entity.PasswordResetToken;
import com.vermau2k01.project_management.entity.Tokens;
import com.vermau2k01.project_management.entity.Users;
import com.vermau2k01.project_management.repository.PasswordResetTokenRepository;
import com.vermau2k01.project_management.repository.RolesRepository;
import com.vermau2k01.project_management.repository.TokensRepository;
import com.vermau2k01.project_management.repository.UsersRepository;
import com.vermau2k01.project_management.request.AuthenticationRequest;
import com.vermau2k01.project_management.request.PasswordResetRequest;
import com.vermau2k01.project_management.request.RegistrationRequest;
import com.vermau2k01.project_management.response.AuthenticationResponse;
import com.vermau2k01.project_management.security.JwtService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {

    private final UsersRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RolesRepository roleRepository;
    private final TokensRepository tokenRepository;
    private final IEmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Override
    public void register(RegistrationRequest request) throws MessagingException {
        var userRole  = roleRepository.findByRole("USER")
                .orElseThrow(()->
                        new IllegalStateException("Role USER was not initalized"));

        var user = Users
                .builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .passcode(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(false)
                .role(List.of(userRole))
                .build();

        userRepository.save(user);
        sendValidationEmail(user);

    }

    private void sendValidationEmail(Users user) throws MessagingException {

        var newToken = generateAndSaveActivationToken(user);
        emailService.sendVerificationEmail(user.getFirstname(), user.getEmail(), newToken);
    }

    private String generateAndSaveActivationToken(Users user) {
        String generatedToken = generatedActivationCode();
        var token = Tokens.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMinutes(5))
                .user(user)
                .build();
        tokenRepository.save(token);
        return generatedToken;
    }

    private String generatedActivationCode() {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < 6; i++) {
            int randomIndex = random.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }
        return codeBuilder.toString();
    }

    @Override
    public AuthenticationResponse login(AuthenticationRequest request) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var claims = new HashMap<String,Object>();
        var user = ((Users)auth.getPrincipal());
        claims.put("fullName", user.getFullName());

        var jwtToken = jwtService.generateToken(claims,user);

        return AuthenticationResponse.builder().token(jwtToken).build();

    }

    @Override
    public void activateAccount(String token) throws MessagingException {

        Tokens savedToken = tokenRepository.findByToken(token)
                .orElseThrow(()->new RuntimeException("Invalid token"));
        if(LocalDateTime.now().isAfter(savedToken.getExpiredAt())) {
            sendValidationEmail(savedToken.getUser());
            throw new RuntimeException("Activation Token expired. " +
                    "A new token has been send");
        }
        var user = userRepository
                .findById(savedToken
                        .getUser()
                        .getId())
                .orElseThrow(()->new RuntimeException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);
        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);

    }

    @Override
    public void requestPasswordReset(PasswordResetRequest request) throws MessagingException {

        Users user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        sendForgotPasswordEmail(user);
    }

    private void sendForgotPasswordEmail(Users user) throws MessagingException {

        var newToken = generateAndSavePasswordResetToken(user);
        emailService.sendPasswordResetEmail(user.getFullName(), user.getEmail(), String.valueOf(newToken));
    }

    private String generateAndSavePasswordResetToken(Users user) {
        String token = generatedActivationCode();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMinutes(5))
                .user(user)
                .build();
        return passwordResetTokenRepository.save(resetToken).getToken();
    }



    @Override
    public void resetPassword(String token, String newPassword) throws MessagingException {

        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (LocalDateTime.now().isAfter(resetToken.getExpiredAt())) {
            sendForgotPasswordEmail(resetToken.getUser());

            throw new RuntimeException("Token expired " +
                    "A new token has been send");
        }

        Users user = userRepository.findById(resetToken.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPasscode(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        resetToken.setValidatedAt(LocalDateTime.now());
        passwordResetTokenRepository.save(resetToken);

    }
}
