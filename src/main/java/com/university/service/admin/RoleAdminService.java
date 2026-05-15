package com.university.service.admin;

import com.university.dto.request.admin.RoleAdminRequestDTO;
import com.university.dto.response.admin.RoleAdminResponseDTO;
import com.university.entity.Role;
import com.university.exception.SimpleMessageException;
import com.university.mapper.admin.RoleAdminMapper;
import com.university.repository.admin.RoleAdminRepository;
import com.university.repository.admin.RolePermissionsAdminRepository;
import com.university.repository.admin.UserRoleAdminRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleAdminService {

    private final RoleAdminRepository roleRepository;
    private final RoleAdminMapper roleAdminMapper;

    public RoleAdminResponseDTO create(RoleAdminRequestDTO dto) {

        if (roleRepository.existsByMaRole(dto.getMaRole()))
            throw new SimpleMessageException("Mã vai trò '" + dto.getMaRole() + "' đã tồn tại!");

        try {
            Role role = roleAdminMapper.toEntity(dto);
            role.setCreatedAt(LocalDateTime.now());
            return roleAdminMapper.toResponseDTO(roleRepository.save(role));

        } catch (Exception e) {
            // log.error("Lỗi khi tạo role: ", e);
            throw new SimpleMessageException("Thêm vai trò không thành công!");
        }
    }

    public List<RoleAdminResponseDTO> createListRole(List<RoleAdminRequestDTO> dto) {
        List<Role> roles = dto.stream().map(item -> {
            if (roleRepository.existsByMaRole(item.getMaRole())) {
                throw new SimpleMessageException(
                        "Ma role " + item.getMaRole() + " đã tồn tại");
            }
            return roleAdminMapper.toEntity(item);
        }).toList();
        List<RoleAdminResponseDTO> drolesResponseDTOs = roles.stream().map(item -> {
            return roleAdminMapper.toResponseDTO(item);
        }).toList();
        roleRepository.saveAll(roles);
        return drolesResponseDTOs;
    }

    public List<RoleAdminResponseDTO> getAll() {
        return roleRepository.getAllRoleDTO();
    }

    public RoleAdminResponseDTO getRoleById(UUID id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy vai trò"));
        return roleAdminMapper.toResponseDTO(role);
    }

    public List<RoleAdminResponseDTO> getByMaRole(String keyword) {
        return roleRepository.findRoleByMaRole(keyword);
    }

    public RoleAdminResponseDTO update(UUID id, RoleAdminRequestDTO dto) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("không tìm thấy vai trò"));
        roleAdminMapper.upDateEntity(role, dto);
        return roleAdminMapper.toResponseDTO(role);
    }

    private final UserRoleAdminRepository u;
    private final RolePermissionsAdminRepository r;

    private boolean hasForeignKey(UUID roleId) {
        return u.existsByRoleId(roleId) || r.existsByRoleId(roleId);
    }

    private void checkCanDelete(UUID roleId) {
        if (u.existsByRoleId(roleId)) {
            throw new SimpleMessageException("Vai trò đang được gán cho người dùng, không thể xóa");
        }
        if (r.existsByRoleId(roleId)) {
            throw new SimpleMessageException("Vai trò đang có quyền liên kết, không thể xóa");
        }
    }

    public void delete(UUID roleId) {
        checkCanDelete(roleId);
        roleRepository.deleteById(roleId);
    }

    @Transactional
    public List<String> deleteAllByList(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }

        List<UUID> deletable = new ArrayList<>();
        List<String> cannotDelete = new ArrayList<>();

        for (UUID id : ids) {
            if (hasForeignKey(id)) {
                String maRole = "Unknown";
                try {
                    maRole = roleRepository.findById(id)
                            .map(Role::getMaRole)
                            .orElse("Unknown");
                } catch (Exception ignored) {}
                cannotDelete.add(maRole);
            } else {
                deletable.add(id);
            }
        }

        if (!deletable.isEmpty()) {
            roleRepository.deleteAllByIdInBatch(deletable);
        }

        return cannotDelete;
    }

}