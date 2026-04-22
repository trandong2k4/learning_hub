package com.university.service.auth;

import com.university.dto.request.auth.RegisterRequest;
import com.university.dto.response.admin.UsersAdminResponseDTO;
import com.university.dto.response.auth.LoginResponseDTO;
import com.university.dto.response.auth.RegisterResponseDTO;
import com.university.entity.Users;
import com.university.exception.SimpleMessageException;
import com.university.repository.admin.PermissionsAdminRepository;
import com.university.repository.admin.UsersAdminRepository;
import com.university.util.JwtUtil;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsersAdminRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;
    private final RefreshTokenService refreshTokenService;
    private final PermissionsAdminRepository pr;

    public AuthService(UsersAdminRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil,
            CustomUserDetailsService cUserDetailsService, RefreshTokenService refreshTokenService,
            PermissionsAdminRepository pRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = cUserDetailsService;
        this.refreshTokenService = refreshTokenService;
        this.pr = pRepository;
    }

    public RegisterResponseDTO register(UUID id, RegisterRequest request) {
        Users user = userRepository.findById(id).orElseThrow();
        user.setUserName(request.getUserName());
        user.setPassWord(passwordEncoder.encode(request.getPassWord()));
        user.setCreateAt(request.getCreateDate());
        user = userRepository.save(user);

        return new RegisterResponseDTO(user.getId(), user.getUsername(), user.getCreateAt());
    }

    @Transactional
    public LoginResponseDTO authenticate(String username, String rawPassword) {

        UsersAdminResponseDTO user = userRepository.findByUserName(username);
        if (user == null) {
            throw new SimpleMessageException("Tài khoản hoặc mật khẩu không đúng");
        }

        if (!Boolean.TRUE.equals(user.getTrangThai())) {
            throw new SimpleMessageException("Tài khoản đã bị khóa! " +
                    (user.getGhiChu() != null ? user.getGhiChu() : ""));
        }

        if (!passwordEncoder.matches(rawPassword, user.getPassWord())) {
            throw new SimpleMessageException("Tài khoản hoặc mật khẩu không đúng");
        }

        // Lấy UserDetails có đầy đủ roles
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        // Tạo access token (15 phút)
        String accessToken = jwtUtil.generateAccessToken(userDetails);

        // Tạo refresh token (7 ngày) - có thể là JWT hoặc random string
        String refreshToken = jwtUtil.generateRefreshToken(username);

        refreshTokenService.saveRefreshToken(user.getUserName(), refreshToken);

        // Lấy roles để trả về frontend (drole)
        List<String> rolesForFrontend = userDetails.getAuthorities().stream()
                .map(auth -> auth.getAuthority().replace("ROLE_", ""))
                .toList();

        List<String> dPermissions = pr.findMaPermissionsByUserId(user.getId());

        new LoginResponseDTO();
        return LoginResponseDTO.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .fullName(user.getHoTen())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .dRole(rolesForFrontend)
                .dPermissions(dPermissions)
                .message("Đăng nhập thành công")
                .build();

    }
}
