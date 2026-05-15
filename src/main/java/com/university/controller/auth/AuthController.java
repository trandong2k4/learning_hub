package com.university.controller.auth;

import com.university.dto.request.auth.*;
import com.university.dto.response.auth.*;
import com.university.exception.SimpleMessageException;
import com.university.service.auth.AuthService;
import com.university.service.auth.PasswordResetService;
import com.university.service.auth.RefreshTokenService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

        private final AuthService authService;
        private final RefreshTokenService refreshTokenService;
        private final PasswordResetService passwordResetService;

        // ================= LOGIN =================
        @PostMapping("/login")
        public ResponseEntity<LoginResponseDTO> login(
                        @Valid @RequestBody LoginRequestDTO request) {

                return ResponseEntity.ok(
                                authService.authenticate(
                                                request.getUsername(),
                                                request.getPassword()));
        }

        // ================= REFRESH =================
        @PostMapping("/refresh")
        public ResponseEntity<?> refreshToken(
                        @RequestBody RefreshRequest request) {

                String refreshToken = request.getRefreshToken();

                String userId = refreshTokenService.getUserId(refreshToken);

                if (userId == null) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                        .body(new MessageResponseDTO("Refresh token không hợp lệ hoặc đã hết hạn"));
                }

                String newAccessToken = authService.generateAccessTokenFromUserId(userId);

                return ResponseEntity.ok(
                                new RefreshResponseDTO(newAccessToken, refreshToken));
        }

        @PostMapping("/logout")
        public ResponseEntity<?> logout(
                        @RequestHeader(value = "Authorization", required = false) String authHeader,
                        @RequestBody(required = false) LogoutRequestDTO logoutRequest) { // Chấp nhận cả khi body trống

                if (logoutRequest == null || logoutRequest.getRefreshToken() == null) {
                        throw new SimpleMessageException("Thiếu Refresh Token để đăng xuất");
                }

                return ResponseEntity.ok(authService.Logout(authHeader, logoutRequest.getRefreshToken()));
        }

        @PostMapping("/forgot-password")
        public ResponseEntity<MessageResponseDTO> forgotPassword(
                        @Valid @RequestBody ForgotPasswordRequestDTO request) {

                passwordResetService.forgotPassword(request);

                return ResponseEntity.ok(
                                new MessageResponseDTO(
                                                "Nếu email tồn tại, hướng dẫn đã được gửi"));
        }

        // ================= RESET PASSWORD =================
        @PostMapping("/reset-password")
        public ResponseEntity<MessageResponseDTO> resetPassword(
                        @Valid @RequestBody ResetPasswordRequestDTO request) {

                passwordResetService.resetPassword(request);

                return ResponseEntity.ok(
                                new MessageResponseDTO("Đặt lại mật khẩu thành công"));
        }
}