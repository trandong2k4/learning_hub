package com.university.interceptor;

import com.university.annotation.RequirePermission;
import com.university.security.CustomUserDetails;
import com.university.service.PermissionsCacheService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PermissionInterceptor implements HandlerInterceptor {

        private final PermissionsCacheService permissionsCacheService;

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
                        throws Exception {

                if (!(handler instanceof HandlerMethod handlerMethod)) {
                        return true;
                }

                RequirePermission classLevel = handlerMethod.getBeanType().getAnnotation(RequirePermission.class);
                RequirePermission methodLevel = handlerMethod.getMethodAnnotation(RequirePermission.class);

                RequirePermission requirePermission = (methodLevel != null) ? methodLevel : classLevel;

                if (requirePermission == null || requirePermission.value().length == 0) {
                        return true;
                }

                String[] requiredPermissions = requirePermission.value();
                List<String> requiredList = Arrays.asList(requiredPermissions);

                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (authentication == null || !authentication.isAuthenticated()) {
                        sendForbidden(response, "Chưa xác thực người dùng");
                        return false;
                }

                Object principal = authentication.getPrincipal();

                if (!(principal instanceof CustomUserDetails userDetails)) {
                        sendForbidden(response, "Không xác định được người dùng");
                        return false;
                }

                // Lấy permissions từ Redis
                List<String> userPermissions = permissionsCacheService.getCachedPermissions(userDetails.getUserId());

                // Nếu chưa có trong cache, lấy từ authorities trong security context
                if (userPermissions == null || userPermissions.isEmpty()) {
                        userPermissions = authentication.getAuthorities().stream()
                                        .map(GrantedAuthority::getAuthority)
                                        .filter(auth -> !auth.startsWith("ROLE_"))
                                        .toList();
                }

                boolean hasPermission = false;

                // Check permissions (các quyền cụ thể như ADMIN_USER_MANAGEMENT)
                for (String required : requiredList) {
                        if (userPermissions.contains(required)) {
                                hasPermission = true;
                                break;
                        }
                }

                if (!hasPermission) {
                        log.warn("User {} denied access to {} {}. Required: {}, Has: {}",
                                        userDetails.getUsername(),
                                        request.getMethod(),
                                        request.getRequestURI(),
                                        requiredList,
                                        userPermissions);
                        sendForbidden(response, "Bạn không có quyền thực hiện thao tác này");
                        return false;
                }

                log.debug("User {} authorized for {} {} with permissions {}",
                                userDetails.getUsername(),
                                request.getMethod(),
                                request.getRequestURI(),
                                requiredList);

                return true;
        }

        private void sendForbidden(HttpServletResponse response, String message) throws Exception {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(
                                "{\"status\":403,\"message\":\"" + message + "\"}");
        }
}
