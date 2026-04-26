package com.university.controller.auth;

import com.university.dto.request.auth.ForgotPasswordRequestDTO;
import com.university.dto.request.auth.LoginRequestDTO;
import com.university.dto.request.auth.LogoutRequestDTO;
import com.university.dto.request.auth.RefreshRequest;
import com.university.dto.request.auth.ResetPasswordRequestDTO;
import com.university.dto.response.auth.LoginResponseDTO;
import com.university.dto.response.auth.MessageResponseDTO;
import com.university.dto.response.auth.RefreshResponseDTO;
import com.university.entity.Users;
import com.university.repository.admin.PermissionsAdminRepository;
import com.university.repository.admin.UsersAdminRepository;
import com.university.service.auth.AuthService;
import com.university.service.auth.CustomUserDetailsService;
import com.university.service.auth.PasswordResetService;
import com.university.service.auth.RefreshTokenService;
import com.university.util.JwtUtil;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

import java.util.List;

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
    private final PasswordResetService passwordResetService;
    private final PermissionsAdminRepository pr;

    public AuthController(AuthService authService,
            RefreshTokenService refreshTokenService,
            JwtUtil jwtUtil,
            CustomUserDetailsService customUserDetailsService,
            PasswordResetService passwordResetService, PermissionsAdminRepository permissionsAdminRepository,
            UsersAdminRepository usersAdminRepository) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
        this.passwordResetService = passwordResetService;
        this.pr = permissionsAdminRepository;
        this.usersAdminRepository = usersAdminRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        LoginResponseDTO response = authService.authenticate(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(response);
    }

    private final UsersAdminRepository usersAdminRepository;

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshRequest request) {

        String refreshToken = request.getRefreshToken();

        if (!jwtUtil.isRefreshTokenValid(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponseDTO("Refresh token không hợp lệ hoặc đã hết hạn"));
        }

        String username = jwtUtil.extractUsername(refreshToken);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        Users users = usersAdminRepository.findByUserName(username)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy users"));

        List<String> permissions = pr.findMaPermissionsByUserId(users.getId());

        String newAccessToken = jwtUtil.generateAccessToken(userDetails, permissions);

        return ResponseEntity.ok(new RefreshResponseDTO(newAccessToken, refreshToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponseDTO> logout(
            @RequestBody LogoutRequestDTO request,
            Authentication authentication) {

        String username = authentication.getName();
        String refreshToken = request.getRefreshToken();

        if (!refreshTokenService.validateRefreshToken(refreshToken, username)) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponseDTO("Refresh token không hợp lệ"));
        }

        refreshTokenService.deleteByToken(refreshToken);

        return ResponseEntity.ok(new MessageResponseDTO("Đăng xuất thành công"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponseDTO> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequestDTO request) {
        passwordResetService.forgotPassword(request);
        return ResponseEntity.ok(
                new MessageResponseDTO("Nếu email tồn tại trong hệ thống, hướng dẫn khôi phục mật khẩu đã được gửi"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponseDTO> resetPassword(
            @Valid @RequestBody ResetPasswordRequestDTO request) {
        passwordResetService.resetPassword(request);
        return ResponseEntity.ok(new MessageResponseDTO("Đặt lại mật khẩu thành công"));
    }
}
