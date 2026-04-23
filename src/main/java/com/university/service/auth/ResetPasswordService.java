package com.university.service.auth;

import com.university.entity.Users;
import com.university.repository.admin.UsersAdminRepository;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ResetPasswordService {

    private final PasswordResetTokenService tokenService;
    private final UsersAdminRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ResetPasswordService(PasswordResetTokenService tokenService,
            UsersAdminRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void resetPassword(String token, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("Password confirmation does not match");
        }

        UUID userId = tokenService.validateToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token is invalid or expired"));

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setPassWord(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        tokenService.deleteToken(token);
    }
}
