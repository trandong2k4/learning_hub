package com.university.service.auth;

import com.university.dto.response.auth.AuthResponseDTO;
import com.university.dto.response.auth.LoginResponseDTO;
import com.university.entity.Users;
import com.university.exception.NotFoundException;
import com.university.exception.SimpleMessageException;
import com.university.repository.admin.UsersAdminRepository;
import com.university.security.CustomUserDetails;
import com.university.service.PermissionsCacheService;
import com.university.util.JwtUtil;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

        private final UsersAdminRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtUtil jwtUtil;
        private final RefreshTokenService refreshTokenService;
        private final PermissionsCacheService permissionsCacheService;

        @Transactional
        public LoginResponseDTO authenticate(String userName, String rawPassword) {
                LoginResponseDTO.UserLoginProjection user = userRepository.findByUserLoginProjection(userName)
                                .orElseThrow(() -> new SimpleMessageException("Tài khoản hoặc mật khẩu không đúng"));
                // Check trạng thái
                if (!Boolean.TRUE.equals(user.isTrangThai())) {
                        throw new SimpleMessageException("Tài khoản đã bị khóa! " +
                                        (user.getGhiChu() != null ? user.getGhiChu() : ""));
                }

                // Check password
                if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
                        throw new SimpleMessageException("Tài khoản hoặc mật khẩu không đúng");
                }

                // Lấy toàn bộ Roles & Permissions trong 1 câu Query duy nhất
                List<AuthResponseDTO> authData = userRepository.findAllRoleAndPermissionsByUserId(user.getId());

                // Tách lấy danh sách Roles (duy nhất)
                List<String> roles = authData.stream()
                                .map(AuthResponseDTO::getMaRole)
                                .distinct()
                                .toList();

                // Tách lấy danh sách Permissions (duy nhất)
                List<String> permissions = authData.stream()
                                .map(AuthResponseDTO::getMaPermissions)
                                .filter(p -> p != null)
                                .distinct()
                                .toList();

                // LƯU PERMISSIONS VÀO REDIS
                permissionsCacheService.cachePermissions(user.getId(), permissions);

                // Tạo danh sách Authorities (Spring Security cần để phân quyền
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                roles.forEach(r -> authorities.add(new SimpleGrantedAuthority("ROLE_" + r)));

                // Build UserDetails
                CustomUserDetails userDetails = new CustomUserDetails(
                                user.getId(),
                                user.getUserName(),
                                user.getPassword(),
                                authorities,
                                null);

                // Generate token
                String accessToken = jwtUtil.generateAccessToken(userDetails);
                String refreshToken = refreshTokenService.createRefreshToken(user.getId().toString());

                // Format role cho FE
                List<String> rolesForFrontend = userRepository.findAllRoleByUserId(user.getId());

                return LoginResponseDTO.builder()
                                .id(user.getId())
                                .userName(user.getUserName())
                                .fullName(user.getHoTen())
                                .accessToken(accessToken)
                                .refreshToken(refreshToken)
                                .dRole(rolesForFrontend)
                                .message("Đăng nhập thành công")
                                .build();
        }

        public String generateAccessTokenFromUserId(String userId) {
                // 1. Tìm user từ database
                Users user = userRepository.findById(UUID.fromString(userId))
                                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng"));

                // 2. Lấy danh sách Roles và Permissions từ Repository
                List<AuthResponseDTO> authData = userRepository.findAllRoleAndPermissionsByUserId(user.getId());

                // Tách lấy danh sách Roles (duy nhất)
                List<String> roles = authData.stream()
                                .map(AuthResponseDTO::getMaRole)
                                .distinct()
                                .toList();

                // Tách lấy danh sách Permissions (duy nhất)
                List<String> permissions = authData.stream()
                                .map(AuthResponseDTO::getMaPermissions)
                                .filter(p -> p != null)
                                .distinct()
                                .toList();

                // CẬP NHẬT PERMISSIONS VÀO REDIS KHI REFRESH TOKEN
                permissionsCacheService.cachePermissions(user.getId(), permissions);

                // 3. Chuyển đổi roles thành GrantedAuthority (có tiền tố ROLE_)
                List<SimpleGrantedAuthority> authorities = roles
                                .stream()
                                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                                .toList();

                // 4. Khởi tạo CustomUserDetails với đầy đủ 5 tham số
                CustomUserDetails userDetails = new CustomUserDetails(
                                user.getId(),
                                user.getUsername(),
                                user.getPassword(),
                                authorities,
                                null);

                // 5. Tạo token thông qua JwtUtil
                return jwtUtil.generateAccessToken(userDetails);
        }

        public String Logout(String authHeader, String refreshToken) {
                String userIdFromToken = null;

                // 1. Lấy userId từ refresh token
                if (refreshToken != null && !refreshToken.isBlank()) {
                        userIdFromToken = refreshTokenService.getUserId(refreshToken);
                        refreshTokenService.deleteToken(refreshToken, userIdFromToken);
                }

                // 2. Nếu không lấy được từ refresh token, thử từ Access Token
                if (userIdFromToken == null) {
                        try {
                                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                                        String token = authHeader.substring(7);
                                        if (token.chars().filter(ch -> ch == '.').count() == 2) {
                                                userIdFromToken = jwtUtil.extractUserId(token);
                                                refreshTokenService.deleteToken(refreshToken, userIdFromToken);
                                        }
                                }
                        } catch (Exception e) {
                                throw new NotFoundException("Không thể trích xuất UserId từ Access Token: " + e.getMessage());
                        }
                }

                // 3. XÓA PERMISSIONS CACHE KHI LOGOUT
                if (userIdFromToken != null) {
                        permissionsCacheService.evictPermissions(UUID.fromString(userIdFromToken));
                }

                return "Đăng xuất thành công";
        }
}
