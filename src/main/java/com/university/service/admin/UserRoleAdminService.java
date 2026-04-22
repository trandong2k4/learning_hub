package com.university.service.admin;

import com.university.dto.request.admin.UserRoleAdminRequestDTO;
import com.university.dto.response.admin.UsersRoleAdminResponseDTO;
import com.university.entity.Role;
import com.university.entity.UserRole;
import com.university.entity.Users;
import com.university.exception.SimpleMessageException;
import com.university.mapper.admin.UserRoleAdminMapper;
import com.university.repository.admin.RoleAdminRepository;
import com.university.repository.admin.UserRoleAdminRepository;
import com.university.repository.admin.UsersAdminRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRoleAdminService {

    private final UsersAdminRepository usersAdminRepository;
    private final RoleAdminRepository roleRepository;
    private final UserRoleAdminMapper userRoleAdminMapper;
    private final UserRoleAdminRepository userRoleAdminRepository;

    public UsersRoleAdminResponseDTO create(UserRoleAdminRequestDTO dto) {
        try {
            Users users = usersAdminRepository.findById(dto.getUsersId()).orElseThrow();
            Role role = roleRepository.findById(dto.getRoleId()).orElseThrow();
            if (userRoleAdminRepository.existsByUsersId(dto.getUsersId())
                    && userRoleAdminRepository.existsByRoleId(dto.getRoleId())) {
                throw new SimpleMessageException("Da ton tai trong BD");
            }

            UserRole userRole = userRoleAdminMapper.toEntity(role, users);
            userRoleAdminRepository.save(userRole);

            // ✅ convert sang DTO
            return userRoleAdminMapper.toResponseDTO(userRole);

        } catch (Exception e) {
            throw new SimpleMessageException("Thêm vai trò cho user không thành công! " + e.getMessage());
        }
    }

}