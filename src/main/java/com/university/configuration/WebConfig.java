package com.university.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${FRONTEND_URL}")
    private String fontendURL;

    @SuppressWarnings("null")
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("**",
                        "http://localhost:5173", // (Tùy chọn) Hoặc port 5173 nếu dùng Vite
                        fontendURL // Cho phép domain Vercel của bạn
                // "http://localhost:3000" // (Tùy chọn) Cho phép cả domain FE local để test
                // "http://localhost:5500", // (Tùy chọn) React dev server khác (nếu dùng)
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false) // Nếu cần cookie/Authorization
                .maxAge(3600);
    }
}
