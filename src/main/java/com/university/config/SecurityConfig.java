package com.university.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtFilter;
        private final CustomAuthenticationEntryPoint entryPoint;
        private final CustomAccessDeniedHandler accessDeniedHandler;

        @Value("${app.cors.allowed-origins}")
        private List<String> allowedOrigins;

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

                http
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .csrf(csrf -> csrf.disable())

                                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/api/auth/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/files/**").permitAll()
                                                .requestMatchers("/actuator/**").permitAll()
                                                .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")
                                                .requestMatchers("/api/student/**")
                                                .hasAnyAuthority("ROLE_STUDENT", "ROLE_ADMIN")
                                                .requestMatchers("/api/lecturer/**")
                                                .hasAnyAuthority("ROLE_LECTURER", "ROLE_ADMIN")
                                                .requestMatchers("/api/accountant/**")
                                                .hasAnyAuthority("ROLE_ACCOUNTANT", "ROLE_ADMIN")
                                                .requestMatchers("/api/notifications/**").authenticated()
                                                .requestMatchers("/api/chatbot/**").authenticated()
                                                .requestMatchers("/api/**").authenticated()
                                                .anyRequest().authenticated())

                                .exceptionHandling(ex -> ex
                                                .authenticationEntryPoint(entryPoint)
                                                .accessDeniedHandler(accessDeniedHandler))

                                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOrigins(allowedOrigins);
                config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
                config.setAllowedHeaders(List.of("*"));
                config.setAllowCredentials(true);
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/api/**", config);
                return source;
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
                return config.getAuthenticationManager();
        }
}
