package com.university.config;

import com.university.service.auth.CustomUserDetailsService;
import com.university.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtUtil jwtUtil;
        private final CustomUserDetailsService userDetailsService; // ← Inject interface

        @Bean
        public JwtAuthenticationFilter jwtAuthenticationFilter() {
                JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil);
                filter.setUserDetailsService(userDetailsService); // ← Setter injection
                return filter;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/api/auth/**",
                                                                "/swagger-ui/**", "/v3/api-docs/**")
                                                .permitAll()

                                                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                                                .requestMatchers("/api/student/**").hasAnyRole("STUDENT", "ADMIN")
                                                .requestMatchers("/api/lecture/**").hasAnyRole("LECTURE", "ADMIN")
                                                .requestMatchers("/api/accounting/**").hasAnyRole("ACCOUNTING", "ADMIN")
                                                .requestMatchers("/api/**").authenticated()
                                                .anyRequest().permitAll())
                                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
                return config.getAuthenticationManager();
        }
}