package com.university.service.admin;

import com.university.dto.request.admin.RolePermissionsAdminRequestDTO;
import com.university.dto.response.admin.RolePermissionsAdminResponseDTO;
import com.university.entity.Permissions;
import com.university.entity.Role;
import com.university.entity.RolePermissions;
import com.university.exception.SimpleMessageException;
import com.university.mapper.admin.RolePermissionsAdminMapper;
import com.university.repository.admin.PermissionsAdminRepository;
import com.university.repository.admin.RoleAdminRepository;
import com.university.repository.admin.RolePermissionsAdminRepository;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RolePermissionsAdminService {

    private final RoleAdminRepository roleAdminRepository;
    private final PermissionsAdminRepository permissionsRepository;
    private final RolePermissionsAdminRepository rolePermissionsRepository;
    private final RolePermissionsAdminMapper mapper;

    public RolePermissionsAdminResponseDTO create(RolePermissionsAdminRequestDTO dto) {
        // 1. Kiểm tra xem quyền và vai trò có tồn tại không
        Permissions permission = permissionsRepository.findById(dto.getPermissionsId())
                .orElseThrow(() -> new SimpleMessageException("Quyền không tồn tại"));
        Role role = roleAdminRepository.findById(dto.getRoleId())
                .orElseThrow(() -> new SimpleMessageException("Vai trò không tồn tại"));

        // 2. Kiểm tra cặp này đã tồn tại trong DB chưa
        boolean exists = rolePermissionsRepository.existsByRoleIdAndPermissionsId(dto.getRoleId(),
                dto.getPermissionsId());
        if (exists) {
            throw new SimpleMessageException("Quyền này đã được gán cho vai trò này rồi!");
        }

        try {
            // 3. Tạo entity mới
            RolePermissions rolePermissions = mapper.toEntity(role, permission);

            rolePermissionsRepository.save(rolePermissions);

            return mapper.toResponseDTO(rolePermissions);
        } catch (Exception e) {
            throw new SimpleMessageException("Lỗi khi gán quyền: " + e.getMessage());
        }
    }

    public List<RolePermissionsAdminResponseDTO> getAllByRole(UUID roleId) {
        Role role = roleAdminRepository.findById(roleId)
                .orElseThrow(() -> new SimpleMessageException("Không tìm thấy vai trò"));
        return rolePermissionsRepository.findPermissionsWithStatusByRoleId(role.getId());
    }

    public void deleteRequest(RolePermissionsAdminRequestDTO dto) {
        try {
            RolePermissions rp = rolePermissionsRepository
                    .findByRoleIdAndPermissionsId(dto.getRoleId(), dto.getPermissionsId())
                    .orElseThrow(() -> new SimpleMessageException("Không tìm thấy mối liên kết để xóa!"));

            rolePermissionsRepository.delete(rp);
        } catch (Exception e) {
            throw new SimpleMessageException("Xóa quyền khỏi vai trò thất bại: " + e.getMessage());
        }
    }

    public void deleteById(UUID id) {
        try {
            RolePermissions rp = rolePermissionsRepository.findById(id)
                    .orElseThrow(() -> new SimpleMessageException("Không tìm thấy mối liên kết để xóa!"));

            rolePermissionsRepository.delete(rp);
        } catch (Exception e) {
            throw new SimpleMessageException("Xóa quyền khỏi vai trò thất bại: " + e.getMessage());
        }
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
            rolePermissionsRepository.deleteAllByIdIn(ids);

        } catch (Exception e) {
            throw new SimpleMessageException("Lỗi khi xóa danh sách: " + e.getMessage());
        }
    }
}