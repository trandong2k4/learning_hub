package com.university.config;

import org.springframework.security.core.context.SecurityContextHolder;
import com.university.security.CustomUserDetails;
import java.util.UUID;

public class SecurityUtils {

    public static UUID getCurrentHocVienId() {
        return getCurrentUserDetails().getUserId();
    }

    public static UUID getCurrentUserId() {
        return getCurrentHocVienId();
    }

    public static CustomUserDetails getCurrentUserDetails() {
        Object principal = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if (principal instanceof CustomUserDetails user) {
            return user;
        }

        throw new RuntimeException("Không lấy được thông tin người dùng hiện tại");
    }
}
