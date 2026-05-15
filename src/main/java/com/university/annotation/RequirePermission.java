package com.university.annotation;

import java.lang.annotation.*;

/**
 * Annotation dùng để xác thực quyền truy cập API.
 * Có thể đặt ở cấp class (áp dụng cho tất cả method) hoặc cấp method.
 * Quyền ở cấp method sẽ ghi đè quyền ở cấp class.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {

    /**
     * Danh sách các quyền cần thiết để truy cập endpoint.
     * Nếu có nhiều quyền, user cần có ÍT NHẤT 1 quyền trong danh sách.
     */
    String[] value() default {};
}
