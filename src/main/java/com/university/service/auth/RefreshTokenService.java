package com.university.service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String REFRESH_TOKEN_KEY_PREFIX = "refresh_token:";
    private static final long REFRESH_EXPIRY_DAYS = 7;

    // Lưu refresh token (key = token)
    public void saveRefreshToken(String username, String refreshToken) {
        String key = REFRESH_TOKEN_KEY_PREFIX + refreshToken;

        redisTemplate.opsForValue().set(
                key,
                username,
                REFRESH_EXPIRY_DAYS,
                TimeUnit.DAYS);
    }

    // Lấy username từ refresh token
    public String getUsernameByToken(String refreshToken) {
        String key = REFRESH_TOKEN_KEY_PREFIX + refreshToken;
        return redisTemplate.opsForValue().get(key);
    }

    // Xóa token khi logout
    public void deleteByToken(String refreshToken) {
        String key = REFRESH_TOKEN_KEY_PREFIX + refreshToken;
        redisTemplate.delete(key);
    }

    // Validate token
    public boolean validateRefreshToken(String refreshToken, String username) {
        String storedUsername = getUsernameByToken(refreshToken);
        return storedUsername != null && storedUsername.equals(username);
    }
}