package com.university.service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String TOKEN_PREFIX = "refresh_token:";
    private static final String USER_SESSION_PREFIX = "user_sessions:";
    private static final long EXPIRY_DAYS = 7;
    private static final int MAX_SESSIONS = 5;

    // 🔥 HASH TOKEN (SHA-256)
    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));

            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();

        } catch (Exception e) {
            throw new RuntimeException("Hash error");
        }
    }

    // 🔥 CREATE TOKEN
    public String createRefreshToken(String userId) {

        String rawToken = UUID.randomUUID().toString();
        String hashedToken = hashToken(rawToken);

        String tokenKey = TOKEN_PREFIX + hashedToken;
        String sessionKey = USER_SESSION_PREFIX + userId;

        // Lưu token → user
        redisTemplate.opsForValue().set(
                tokenKey,
                userId,
                EXPIRY_DAYS,
                TimeUnit.DAYS);

        // Lưu session list
        redisTemplate.opsForSet().add(sessionKey, hashedToken);
        redisTemplate.expire(sessionKey, EXPIRY_DAYS, TimeUnit.DAYS);

        // 🔥 Limit số session
        Set<String> sessions = redisTemplate.opsForSet().members(sessionKey);
        if (sessions != null && sessions.size() > MAX_SESSIONS) {
            String oldest = sessions.iterator().next();
            redisTemplate.delete(TOKEN_PREFIX + oldest);
            redisTemplate.opsForSet().remove(sessionKey, oldest);
        }

        return rawToken;
    }

    // 🔥 VALIDATE TOKEN
    public boolean validate(String rawToken, String userId) {

        String hashed = hashToken(rawToken);
        String key = TOKEN_PREFIX + hashed;

        String storedUser = redisTemplate.opsForValue().get(key);

        return storedUser != null && storedUser.equals(userId);
    }

    // 🔥 GET USER
    public String getUserId(String rawToken) {
        return redisTemplate.opsForValue()
                .get(TOKEN_PREFIX + hashToken(rawToken));
    }

    // 🔥 LOGOUT (1 device)
    public void deleteToken(String rawToken, String userId) {

        String hashed = hashToken(rawToken);

        redisTemplate.delete(TOKEN_PREFIX + hashed);
        redisTemplate.opsForSet()
                .remove(USER_SESSION_PREFIX + userId, hashed);
    }

    // 🔥 LOGOUT ALL DEVICES
    public void deleteAllByUser(String userId) {

        String sessionKey = USER_SESSION_PREFIX + userId;

        Set<String> sessions = redisTemplate.opsForSet().members(sessionKey);

        if (sessions != null) {
            for (String token : sessions) {
                redisTemplate.delete(TOKEN_PREFIX + token);
            }
        }

        redisTemplate.delete(sessionKey);
    }
}