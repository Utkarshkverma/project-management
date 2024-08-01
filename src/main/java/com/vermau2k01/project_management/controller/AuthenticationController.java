package com.vermau2k01.project_management.controller;

import com.vermau2k01.project_management.request.AuthenticationRequest;
import com.vermau2k01.project_management.request.PasswordResetRequest;
import com.vermau2k01.project_management.request.RegistrationRequest;
import com.vermau2k01.project_management.request.ResetPasswordRequest;
import com.vermau2k01.project_management.response.AuthenticationResponse;
import com.vermau2k01.project_management.service.IAuthService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("auth")
public class AuthenticationController {

    private final IAuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> registerUser(@Valid @RequestBody
                                              RegistrationRequest request) throws MessagingException {
        authService.register(request);
        return ResponseEntity.accepted().build();

    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse>
    authenticate(@RequestBody @Valid AuthenticationRequest request)
    {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/activate-account")
    public void confirm(@RequestParam String token) throws MessagingException {
        authService.activateAccount(token);
    }

    @PostMapping("/forgot-password")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> requestPasswordReset(@RequestBody @Valid PasswordResetRequest request) throws MessagingException {
        authService.requestPasswordReset(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestBody @Valid ResetPasswordRequest request) throws MessagingException {
        authService.resetPassword(token, request.getPassword());
        return ResponseEntity.ok().build();
    }


}
