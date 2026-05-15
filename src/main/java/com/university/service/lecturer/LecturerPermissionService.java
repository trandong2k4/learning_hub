package com.university.service.lecturer;

import com.university.entity.Users;
import com.university.repository.admin.UserRoleAdminRepository;
import com.university.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service("permissionService")
@RequiredArgsConstructor
public class LecturerPermissionService {

    private final UserRoleAdminRepository userRoleRepository;

    public boolean hasPermission(UUID userId, String permission) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        if (isAdmin(authentication)) {
            return true;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof Users user) {
            if (!user.getId().equals(userId)) {
                return false;
            }
        } else if (principal instanceof CustomUserDetails user) {
            if (!user.getUserId().equals(userId)) {
                return false;
            }
        }

        Set<String> userPermissions = getUserPermissions(userId);
        return userPermissions.contains(permission);
    }

    public Set<String> getUserPermissions(UUID userId) {
        List<String> rolePermissions = userRoleRepository.findPermissionsByUserId(userId);
        return new HashSet<>(rolePermissions);
    }

    public boolean isOwnerOrAdmin(UUID userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof Users user) {
            if (user.getId().equals(userId)) {
                return true;
            }
        }

        return isAdmin(authentication);
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
