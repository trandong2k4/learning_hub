package com.university.config;

import com.university.security.CustomUserDetails;
import com.university.service.PermissionsCacheService;
import com.university.util.JwtUtil;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

        private final JwtUtil jwtUtil;
        private final PermissionsCacheService permissionsCacheService;

        @Override
        protected void doFilterInternal(HttpServletRequest request,
                        HttpServletResponse response,
                        FilterChain filterChain)
                        throws ServletException, IOException {

                String header = request.getHeader("Authorization");

                if (header != null && header.startsWith("Bearer ")) {
                        String token = header.substring(7);

                        // Kiểm tra cấu trúc JWT (tránh lỗi 500 khi gửi UUID của Refresh Token vào đây)
                        long dotCount = token.chars().filter(ch -> ch == '.').count();
                        if (dotCount != 2) {
                                log.warn("JWT error: Invalid structure at URI: {}", request.getRequestURI());
                                filterChain.doFilter(request, response);
                                return;
                        }

                        try {
                                String username = jwtUtil.extractUsername(token);
                                String userIdStr = jwtUtil.extractUserId(token);
                                UUID userId = UUID.fromString(userIdStr);
                                List<String> roles = jwtUtil.extractRoles(token);

                                // LẤY PERMISSIONS TỪ REDIS THÔNG QUA PERMISSIONS CACHE SERVICE
                                List<String> permissions = permissionsCacheService.getCachedPermissions(userId);

                                // Nếu permissions chưa có trong Redis (cache miss — có thể do vừa bị evict
                                // khi admin thay đổi phân quyền), đọc lại từ DB và cache lại
                                if (permissions == null) {
                                        log.debug("Permissions cache miss for user {}, reloading from DB", userId);
                                        permissions = permissionsCacheService.reloadAndCachePermissions(userId);
                                }

                                // Tạo danh sách Authorities gồm cả Roles và Permissions
                                List<SimpleGrantedAuthority> authorities = new java.util.ArrayList<>();

                                // Thêm Roles (Ví dụ: ROLE_ADMIN)
                                if (roles != null) {
                                        roles.forEach(role -> {
                                                String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;
                                                authorities.add(new SimpleGrantedAuthority(authority));
                                        });
                                }

                                // Thêm Permissions (Ví dụ: USER_CREATE)
                                if (permissions != null) {
                                        permissions.forEach(perm -> authorities.add(new SimpleGrantedAuthority(perm)));
                                }

                                // BUILD USER DETAILS
                                CustomUserDetails userDetails = new CustomUserDetails(
                                                userId,
                                                username,
                                                "",
                                                authorities,
                                                null);

                                // SET VÀO SECURITY CONTEXT
                                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                                userDetails,
                                                null,
                                                authorities);

                                SecurityContextHolder.getContext().setAuthentication(auth);

                        } catch (Exception e) {
                                log.error("JWT validation failed: {}", e.getMessage());
                                SecurityContextHolder.clearContext();
                        }
                }

                filterChain.doFilter(request, response);
        }
}
