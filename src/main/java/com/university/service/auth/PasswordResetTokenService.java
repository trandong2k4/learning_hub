package com.university.service.auth;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetTokenService {

    private static final String PREFIX = "reset_password:";
    private static final Duration TTL = Duration.ofMinutes(15);

    private final StringRedisTemplate redisTemplate;
    private final SecureRandom secureRandom = new SecureRandom();

    public PasswordResetTokenService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String createToken(UUID userId) {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        redisTemplate.opsForValue().set(PREFIX + token, String.valueOf(userId), TTL);
        return token;
    }

    public Optional<UUID> validateToken(String token) {
        String value = redisTemplate.opsForValue().get(PREFIX + token);
        if (value == null) {
            return Optional.empty();
        }
        return Optional.of(UUID.randomUUID());
    }

    public void deleteToken(String token) {
        redisTemplate.delete(PREFIX + token);
    }
}
