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
import com.university.service.PermissionsCacheService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserRoleAdminService {

        private final UsersAdminRepository usersAdminRepository;
        private final RoleAdminRepository roleRepository;
        private final UserRoleAdminMapper userRoleAdminMapper;
        private final UserRoleAdminRepository userRoleAdminRepository;
        private final PermissionsCacheService permissionsCacheService;

    public List<UsersRoleAdminResponseDTO> getAll() {
        return userRoleAdminRepository.getAllDTO();
    }

    public UsersRoleAdminResponseDTO getById(UUID id) {
        UserRole userRole = userRoleAdminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy UserRole"));
        return userRoleAdminMapper.toResponseDTO(userRole, userRole.getUsers(), userRole.getRole());
    }

    public List<UsersRoleAdminResponseDTO> getByUserId(UUID usersId) {
        return userRoleAdminRepository.findAllDTOByUsersId(usersId);
    }

    public UsersRoleAdminResponseDTO getByRoleId(UUID roleId) {
        UserRole us = userRoleAdminRepository.findByRoleId(roleId);
        return userRoleAdminMapper.toResponseDTO(us, us.getUsers(),
                us.getRole());
    }

    public UsersRoleAdminResponseDTO create(UserRoleAdminRequestDTO request) {
        if (userRoleAdminRepository.existsByUsersIdAndRoleId(request.getUsersId(), request.getRoleId())) {
            throw new SimpleMessageException("Người dùng đã có vai trò này");
        }
        Users users = usersAdminRepository.findById(request.getUsersId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài khoản"));
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy vai trò"));

        UserRole userRole = userRoleAdminMapper.toEntity(role, users);
        UserRole saved = userRoleAdminRepository.save(userRole);
        // Invalidate permissions cache for this user
        permissionsCacheService.evictAllForUserRoleChange(users.getId());
        return userRoleAdminMapper.toResponseDTO(saved, saved.getUsers(), saved.getRole());
    }

    @Transactional
    public List<UsersRoleAdminResponseDTO> createListUserRole(List<UserRoleAdminRequestDTO> requests) {
        List<UserRole> list = requests.stream().map(req -> {
            Users users = usersAdminRepository.findById(req.getUsersId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài khoản"));
            Role role = roleRepository.findById(req.getRoleId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy vai trò"));

            return userRoleAdminMapper.toEntity(role, users);
        }).toList();

        List<UserRole> savedList = userRoleAdminRepository.saveAll(list);

        // Invalidate permissions cache for all affected users
        requests.forEach(req -> permissionsCacheService.evictAllForUserRoleChange(req.getUsersId()));

        return savedList.stream()
                .map(m -> userRoleAdminMapper.toResponseDTO(m, m.getUsers(), m.getRole()))
                .toList();
    }

    /**
     * Xóa user-role theo ID
     */
    public void delete(UUID id) {
        UserRole userRole = userRoleAdminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy UserRole"));
        UUID userId = userRole.getUsers().getId();
        userRoleAdminRepository.delete(userRole);
        // Invalidate permissions cache for this user
        permissionsCacheService.evictAllForUserRoleChange(userId);
    }

    /**
     * Xóa nhiều user-role
     */
    @Transactional
    public void deleteAll(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        try {
            // Collect affected userIds before deletion
            List<UUID> affectedUserIds = new ArrayList<>();
            for (UUID id : ids) {
                userRoleAdminRepository.findById(id).ifPresent(ur -> {
                    affectedUserIds.add(ur.getUsers().getId());
                });
            }
            userRoleAdminRepository.deleteAllByIdInBatch(ids);
            // Invalidate permissions cache for all affected users
            affectedUserIds.forEach(permissionsCacheService::evictAllForUserRoleChange);
        } catch (Exception e) {
            throw new SimpleMessageException("Lỗi khi xóa danh sách: " + e.getMessage());
        }
    }
}