package com.university.service.auth;

import com.university.dto.response.admin.UsersAdminResponseDTO;
import com.university.dto.response.auth.LoginResponseDTO;
import com.university.exception.SimpleMessageException;
import com.university.repository.admin.PermissionsAdminRepository;
import com.university.repository.admin.UsersAdminRepository;
import com.university.util.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    private final UsersAdminRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;
    private final RefreshTokenService refreshTokenService;
    private final PermissionsAdminRepository pr;

    public AuthService(UsersAdminRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil,
            CustomUserDetailsService cUserDetailsService,
            RefreshTokenService refreshTokenService,
            PermissionsAdminRepository pRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = cUserDetailsService;
        this.refreshTokenService = refreshTokenService;
        this.pr = pRepository;
    }

    @Transactional
    public LoginResponseDTO authenticate(String username, String rawPassword) {

        UsersAdminResponseDTO user = userRepository.findByUserNameDTO(username);
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

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        List<String> permissions = pr.findMaPermissionsByUserId(user.getId());

        String accessToken = jwtUtil.generateAccessToken(userDetails, permissions);
        String refreshToken = jwtUtil.generateRefreshToken(username);

        refreshTokenService.saveRefreshToken(user.getUserName(), refreshToken);

        List<String> rolesForFrontend = userDetails.getAuthorities().stream()
                .map(auth -> auth.getAuthority().replace("ROLE_", ""))
                .toList();

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
}
