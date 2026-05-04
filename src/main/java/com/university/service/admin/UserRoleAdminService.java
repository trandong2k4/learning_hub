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

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

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

    public UsersRoleAdminResponseDTO create(UserRoleAdminRequestDTO request) {
        Users users = usersAdminRepository.findById(request.getUsersId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài khoản"));
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy vai trò"));

        UserRole userRole = userRoleAdminMapper.toEntity(role, users);
        return userRoleAdminMapper.toResponseDTO(userRoleAdminRepository.save(userRole));
    }

    @Transactional
    public List<UsersRoleAdminResponseDTO> createListUserRole(List<UserRoleAdminRequestDTO> requests) {

        List<UserRole> list = requests.stream().map(req -> {

            Users users = usersAdminRepository.findById(req.getUsersId()).orElseThrow();
            Role role = roleRepository.findById(req.getUsersId()).orElseThrow();

            if (usersAdminRepository.existsById(req.getUsersId())) {
                throw new EntityNotFoundException("Users or học không tồn tại");
            }

            if (users == null || role == null) {
                throw new EntityNotFoundException("Users or role không tồn tại");
            }
            UserRole userRole = userRoleAdminMapper.toEntity(role, users);

            return userRole;

        }).toList();

        List<UserRole> savedList = userRoleAdminRepository.saveAll(list);

        return savedList.stream()
                .map(userRoleAdminMapper::toResponseDTO)
                .toList();
    }

    public void delete(UUID id) {
        UserRole userRole = userRoleAdminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy UserRole"));
        userRoleAdminRepository.delete(userRole);
    }

    @Transactional
    public void deleteAllByList(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        try {
            // Kiem tra user dang co trong cac db khac khong
            // for (UUID uuid : ids) {
            // if (usersAdminRepository.) {

            // }
            // }
            usersAdminRepository.deleteAllByIdIn(ids);

        } catch (Exception e) {
            throw new SimpleMessageException("Lỗi khi xóa danh sách: " + e.getMessage());
        }
    }

}