package com.university.service.auth;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RefreshTokenService {

    private final RedisTemplate<String, String> redisTemplate; // dùng String cho đơn giản

    private static final String REFRESH_TOKEN_KEY_PREFIX = "refresh_token:";
    private static final long REFRESH_EXPIRY_DAYS = 7; // 7 ngày

    public RefreshTokenService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Lưu refresh token vào Redis
    public void saveRefreshToken(String username, String refreshToken) {
        String key = REFRESH_TOKEN_KEY_PREFIX + username;
        redisTemplate.opsForValue().set(key, refreshToken, REFRESH_EXPIRY_DAYS, TimeUnit.DAYS);
    }

    // Lấy refresh token từ Redis
    public String getRefreshToken(String username) {
        String key = REFRESH_TOKEN_KEY_PREFIX + username;
        return redisTemplate.opsForValue().get(key);
    }

    // Xóa refresh token khi logout
    public void deleteRefreshToken(String username) {
        String key = REFRESH_TOKEN_KEY_PREFIX + username;
        redisTemplate.delete(key);
    }

    // Kiểm tra refresh token có hợp lệ
    public boolean validateRefreshToken(String username, String refreshToken) {
        String storedToken = getRefreshToken(username);
        return storedToken != null && storedToken.equals(refreshToken);
    }
}