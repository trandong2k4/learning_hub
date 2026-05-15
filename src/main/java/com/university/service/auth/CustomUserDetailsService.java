package com.university.service.auth;

import com.university.entity.Users;
import com.university.repository.admin.PermissionsAdminRepository;
import com.university.repository.admin.UsersAdminRepository;
import com.university.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsersAdminRepository usersAdminRepository;
    private final PermissionsAdminRepository permissionsAdminRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {

        Users user = usersAdminRepository.findByUsernameWithRoles(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng"));
        List<String> permissions = permissionsAdminRepository.findMaPermissionsByUserId(user.getId());

        return new CustomUserDetails(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getAuthorities(),
                permissions);

    }
}