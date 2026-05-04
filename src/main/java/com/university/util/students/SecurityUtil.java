package com.university.util.students;

import com.university.exception.SimpleMessageException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    private SecurityUtil() {}

    // Lấy userName từ JWT subject
    public static String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()
                || auth.getPrincipal().equals("anonymousUser")) {
            throw new SimpleMessageException("Bạn chưa đăng nhập");
        }

        return auth.getName(); // trả về userName (subject trong JWT)
    }
}