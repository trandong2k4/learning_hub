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
import com.university.service.PermissionsCacheService;

import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RolePermissionsAdminService {

        private final RoleAdminRepository roleAdminRepository;
        private final PermissionsAdminRepository permissionsRepository;
        private final RolePermissionsAdminRepository rolePermissionsRepository;
        private final RolePermissionsAdminMapper mapper;
        private final PermissionsCacheService permissionsCacheService;

    /**
     * Batch sync: set exact permissions for a role.
     * - Unassigns any permissions NOT in grantedIds.
     * - Assigns any permissions in grantedIds NOT currently assigned.
     * Single evict at the end.
     */
    @Transactional
    public void syncPermissions(UUID roleId, List<UUID> grantedIds) {
        Role role = roleAdminRepository.findById(roleId)
                .orElseThrow(() -> new SimpleMessageException("Vai trò không tồn tại"));

        // 1 query: all current RolePermissions for this role
        List<RolePermissions> current = rolePermissionsRepository.findAllByRoleId(roleId);
        Set<UUID> currentPermIds = current.stream()
                .map(rp -> rp.getPermissions().getId())
                .collect(Collectors.toSet());

        Set<UUID> targetIds = grantedIds != null
                ? new HashSet<>(grantedIds)
                : new HashSet<>();

        // 1 query: permissions to insert
        List<UUID> toGrant = targetIds.stream()
                .filter(id -> !currentPermIds.contains(id))
                .toList();

        // 1 query: RolePermissions to delete
        List<RolePermissions> toRevoke = current.stream()
                .filter(rp -> !targetIds.contains(rp.getPermissions().getId()))
                .toList();

        if (!toGrant.isEmpty()) {
            List<Permissions> perms = permissionsRepository.findAllById(toGrant);
            List<RolePermissions> entities = perms.stream()
                    .map(p -> {
                        RolePermissions rp = new RolePermissions();
                        rp.setRole(role);
                        rp.setPermissions(p);
                        return rp;
                    })
                    .toList();
            rolePermissionsRepository.saveAll(entities);
        }

        if (!toRevoke.isEmpty()) {
            List<UUID> revokeIds = toRevoke.stream().map(RolePermissions::getId).toList();
            rolePermissionsRepository.deleteAllByIdIn(revokeIds);
        }

        // 1 evict for all affected users — done only once
        if (!toGrant.isEmpty() || !toRevoke.isEmpty()) {
            permissionsCacheService.evictAllForRoleChange(roleId);
        }
    }

    public RolePermissionsAdminResponseDTO create(RolePermissionsAdminRequestDTO dto) {
        Permissions permission = permissionsRepository.findById(dto.getPermissionsId())
                .orElseThrow(() -> new SimpleMessageException("Quyền không tồn tại"));
        Role role = roleAdminRepository.findById(dto.getRoleId())
                .orElseThrow(() -> new SimpleMessageException("Vai trò không tồn tại"));

        boolean exists = rolePermissionsRepository.existsByRoleIdAndPermissionsId(dto.getRoleId(),
                dto.getPermissionsId());
        if (exists) {
            throw new SimpleMessageException("Quyền này đã được gán cho vai trò này rồi!");
        }

        try {
            RolePermissions rolePermissions = mapper.toEntity(role, permission);
            rolePermissionsRepository.save(rolePermissions);
            // Invalidate permissions cache for all users with this role
            permissionsCacheService.evictAllForRoleChange(role.getId());
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

            UUID roleId = rp.getRole().getId();
            rolePermissionsRepository.delete(rp);
            // Invalidate permissions cache for all users with this role
            permissionsCacheService.evictAllForRoleChange(roleId);
        } catch (Exception e) {
            throw new SimpleMessageException("Xóa quyền khỏi vai trò thất bại: " + e.getMessage());
        }
    }

    public void deleteById(UUID id) {
        try {
            RolePermissions rp = rolePermissionsRepository.findById(id)
                    .orElseThrow(() -> new SimpleMessageException("Không tìm thấy mối liên kết để xóa!"));

            UUID roleId = rp.getRole().getId();
            rolePermissionsRepository.delete(rp);
            // Invalidate permissions cache for all users with this role
            permissionsCacheService.evictAllForRoleChange(roleId);
        } catch (Exception e) {
            throw new SimpleMessageException("Xóa quyền khỏi vai trò thất bại: " + e.getMessage());
        }
    }

    @Transactional
    public List<String> deleteAllByList(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> cannotDelete = new ArrayList<>();

        for (UUID id : ids) {
            try {
                if (!rolePermissionsRepository.existsById(id)) {
                    cannotDelete.add("ID không tồn tại: " + id);
                }
            } catch (Exception e) {
                cannotDelete.add("ID lỗi: " + id);
            }
        }

        if (cannotDelete.isEmpty()) {
            rolePermissionsRepository.deleteAllByIdIn(ids);
            // Invalidate permissions cache for all affected roles
            for (UUID id : ids) {
                rolePermissionsRepository.findById(id).ifPresent(rp -> {
                    permissionsCacheService.evictAllForRoleChange(rp.getRole().getId());
                });
            }
        }

        return cannotDelete;
    }
}