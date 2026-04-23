package com.university.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration:900000}") // 15 phút cho Access Token
    private long accessTokenExpirationMs;

    @Value("${jwt.refresh-expiration:604800000}") // 7 ngày cho Refresh Token (604800000 ms)
    private long refreshTokenExpirationMs;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Tạo Access Token (ngắn hạn)
    public String generateAccessToken(UserDetails userDetails, List<String> permissions) {
        Map<String, Object> claims = new HashMap<>();

        // roles
        claims.put("roles", userDetails.getAuthorities()
                .stream()
                .map(auth -> auth.getAuthority())
                .toList());

        claims.put("permissions", permissions);

        claims.put("tokenType", "access");

        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpirationMs))
                .issuer("university-app")
                .audience().add("university-api").and()
                .signWith(getSigningKey())
                .compact();
    }

    // Tạo Refresh Token (dài hạn)
    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpirationMs))
                .issuer("university-app")
                .claim("tokenType", "refresh")
                .signWith(getSigningKey())
                .compact();
    }

    public List<String> extractPermissions(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("permissions", List.class);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .requireIssuer("university-app")
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isAccessToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return "access".equals(claims.get("tokenType"));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
        } catch (Exception e) {
            logger.debug("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Kiểm tra token có phải refresh token không
    public boolean isRefreshToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return "refresh".equals(claims.get("tokenType"));
        } catch (Exception e) {
            return false;
        }
    }
}