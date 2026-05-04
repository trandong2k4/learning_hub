package com.university.config;

import org.springframework.security.core.context.SecurityContextHolder;
import com.university.security.CustomUserDetails;
import java.util.UUID;
public class SecurityUtils {

    public static UUID getCurrentHocVienId() {
        Object principal = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if (principal instanceof CustomUserDetails user) {
            return user.getHocVienId();
        }

        throw new RuntimeException("Không lấy được hocVienId");
    }
}