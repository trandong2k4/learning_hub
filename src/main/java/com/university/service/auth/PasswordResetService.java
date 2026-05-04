package com.university.service.auth;

import com.university.dto.request.auth.ForgotPasswordRequestDTO;
import com.university.dto.request.auth.ResetPasswordRequestDTO;
import com.university.entity.Users;
import com.university.exception.SimpleMessageException;
import com.university.repository.admin.UsersAdminRepository;
import com.university.service.mail.SendGridMailService;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.UUID;

@Service
public class PasswordResetService {

    private static final String RESET_PASSWORD_PREFIX = "reset_password:";
    private static final Duration RESET_PASSWORD_TTL = Duration.ofMinutes(15);

    private final UsersAdminRepository usersAdminRepository;
    private final StringRedisTemplate redisTemplate;
    private final SendGridMailService sendGridMailService;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetService(UsersAdminRepository usersAdminRepository,
            StringRedisTemplate redisTemplate,
            SendGridMailService sendGridMailService,
            PasswordEncoder passwordEncoder) {
        this.usersAdminRepository = usersAdminRepository;
        this.redisTemplate = redisTemplate;
        this.sendGridMailService = sendGridMailService;
        this.passwordEncoder = passwordEncoder;
    }

    public void forgotPassword(ForgotPasswordRequestDTO request) {
        String rateLimitKey = "rate_limit:forgot_password:" + request.getEmail();

        // Kiểm tra xem email này có đang trong thời gian chờ không
        if (Boolean.TRUE.equals(redisTemplate.hasKey(rateLimitKey))) {
            throw new SimpleMessageException("Vui lòng đợi vài phút trước khi yêu cầu lại");
        }

        usersAdminRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            if (!user.isTrangThai()) {
                return;
            }

            String token = generateSecureToken();
            System.out.println("TOKEN: " + token);
            String redisKey = RESET_PASSWORD_PREFIX + token;

            redisTemplate.opsForValue().set(
                    redisKey,
                    user.getId().toString(),
                    RESET_PASSWORD_TTL);

            sendGridMailService.sendResetPasswordEmail(user.getEmail(), token);
            // Đặt key chặn spam trong vòng 2 phút
            redisTemplate.opsForValue().set(rateLimitKey, "true", Duration.ofMinutes(2));
        });
    }

    public void resetPassword(ResetPasswordRequestDTO request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new SimpleMessageException("Mật khẩu xác nhận không khớp");
        }

        String redisKey = RESET_PASSWORD_PREFIX + request.getToken();
        String userIdValue = redisTemplate.opsForValue().get(redisKey);

        if (userIdValue == null || userIdValue.isBlank()) {
            throw new SimpleMessageException("Token không hợp lệ hoặc đã hết hạn");
        }

        UUID userId;
        try {
            userId = UUID.fromString(userIdValue);
        } catch (IllegalArgumentException e) {
            throw new SimpleMessageException("Token không hợp lệ hoặc đã hết hạn");
        }

        Users user = usersAdminRepository.findById(userId)
                .orElseThrow(() -> new SimpleMessageException("Người dùng không tồn tại"));

        user.setPassWord(passwordEncoder.encode(request.getNewPassword()));
        usersAdminRepository.save(user);

        redisTemplate.delete(redisKey);
    }

    private String generateSecureToken() {
        byte[] randomBytes = new byte[32];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}
