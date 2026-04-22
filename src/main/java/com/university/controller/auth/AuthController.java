package com.university.controller.auth;

import com.university.dto.request.auth.LoginRequestDTO;
import com.university.dto.request.auth.RefreshRequest;
import com.university.dto.request.auth.RegisterRequest;
import com.university.dto.response.auth.LoginResponseDTO;
import com.university.dto.response.auth.RefreshResponseDTO;
import com.university.dto.response.auth.RegisterResponseDTO;
import com.university.service.auth.AuthService;
import com.university.service.auth.CustomUserDetailsService;
import com.university.service.auth.RefreshTokenService;
import com.university.util.JwtUtil;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, RefreshTokenService refreshTokenService, JwtUtil jwtUtil,
            CustomUserDetailsService customUserDetailsService) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {
        LoginResponseDTO response = authService.authenticate(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@RequestBody RegisterRequest request, UUID id) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(id, request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshRequest request) {
        String username = request.getUsername();
        String refreshToken = request.getRefreshToken();

        if (!refreshTokenService.validateRefreshToken(username, refreshToken)) {
            return ResponseEntity.status(401).body("Refresh token không hợp lệ hoặc đã hết hạn");
        }

        // Lấy UserDetails có đầy đủ roles
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        // // Tạo access token mới
        String newAccessToken = jwtUtil.generateAccessToken(userDetails);

        return ResponseEntity.ok(new RefreshResponseDTO(newAccessToken, refreshToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody String refreshToken, Authentication authentication) {
        String username = authentication.getName();
        refreshTokenService.deleteRefreshToken(username);
        return ResponseEntity.ok("Đăng xuất thành công");
    }
}
