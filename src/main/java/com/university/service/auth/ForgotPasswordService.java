package com.university.service.auth;

import com.university.entity.Users;
import com.university.repository.admin.UsersAdminRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;

@Service
public class ForgotPasswordService {

    private final UsersAdminRepository userRepository;
    private final PasswordResetTokenService tokenService;
    private final MailService mailService;

    public ForgotPasswordService(UsersAdminRepository userRepository,
            PasswordResetTokenService tokenService,
            MailService mailService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.mailService = mailService;
    }

    public void forgotPassword(String email) {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("K tìm thấy user"));
        String token = tokenService.createToken(user.getId());
        mailService.sendResetPasswordMail(user.getEmail(), token);
    }
}
