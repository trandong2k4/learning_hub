package com.university.config;

import com.university.entity.Permissions;
import com.university.entity.Role;
import com.university.entity.RolePermissions;
import com.university.enums.AccountantPermission;
import com.university.repository.admin.PermissionsAdminRepository;
import com.university.repository.admin.RoleAdminRepository;
import com.university.repository.admin.RolePermissionsAdminRepository;
import com.university.service.PermissionsCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(prefix = "app.permissions", name = "bootstrap-enabled", havingValue = "true", matchIfMissing = true)
public class AccountantPermissionsInitializer {

        private final PermissionsAdminRepository permissionsRepository;
        private final RoleAdminRepository roleRepository;
        private final RolePermissionsAdminRepository rolePermissionsRepository;
        private final PermissionsCacheService permissionsCacheService;

        private static final String ACCOUNTANT_ROLE = "ACCOUNTANT";

        @Bean
        public ApplicationRunner accountantPermissionsRunner(
                        @Value("${app.permissions.auto-assign:false}") boolean autoAssign) {
                return args -> {

                        List<String> permissionCodes = Arrays.stream(AccountantPermission.values())
                                        .map(Enum::name)
                                        .toList();

                        Set<String> existingCodes = new HashSet<>(permissionsRepository.findAllMaPermissions());

                        List<Permissions> toSave = Arrays.stream(AccountantPermission.values())
                                        .filter(permission -> !existingCodes.contains(permission.name()))
                                        .map(permission -> {
                                                Permissions entity = new Permissions();
                                                entity.setMaPermissions(permission.name());
                                                entity.setMoTa(permission.getMoTa());
                                                return entity;
                                        })
                                        .toList();

                        if (!toSave.isEmpty()) {
                                permissionsRepository.saveAll(toSave);
                                toSave.forEach(permission -> log.info("Created permission: {} - {}",
                                                permission.getMaPermissions(), permission.getMoTa()));
                        }

                        if (!autoAssign) {
                                log.info("PERMISSIONS_AUTO_ASSIGN=false — bo qua gan quyen vao role");
                                log.info("=== Accountant Permissions Initialization Complete ({} permissions created) ===",
                                                permissionCodes.size());
                                return;
                        }

                        Role accountantRole = roleRepository.findFirstByMaRoleIgnoreCase(ACCOUNTANT_ROLE)
                                        .orElseGet(() -> {
                                                Role newRole = new Role();
                                                newRole.setMaRole(ACCOUNTANT_ROLE);
                                                newRole.setMoTa("Ke toan vien");
                                                Role saved = roleRepository.save(newRole);
                                                log.info("Created role: {}", ACCOUNTANT_ROLE);
                                                return saved;
                                        });

                        List<Permissions> allPermissions = permissionsRepository.findByMaPermissionsIn(permissionCodes);
                        Map<String, Permissions> permissionMap = allPermissions.stream()
                                        .collect(Collectors.toMap(Permissions::getMaPermissions,
                                                        permission -> permission));

                        List<UUID> permissionIds = allPermissions.stream()
                                        .map(Permissions::getId)
                                        .toList();

                        Set<UUID> assignedIds = rolePermissionsRepository.findAssignedPermissionsIdsByRoleId(accountantRole.getId(), permissionIds);

                        List<RolePermissions> toAssign = Arrays.stream(AccountantPermission.values())
                                        .filter(permission -> !assignedIds
                                                        .contains(permissionMap.get(permission.name()).getId()))
                                        .map(permission -> {
                                                RolePermissions rolePermission = new RolePermissions();
                                                rolePermission.setRole(accountantRole);
                                                rolePermission.setPermissions(permissionMap.get(permission.name()));
                                                return rolePermission;
                                        })
                                        .toList();

                        if (!toAssign.isEmpty()) {
                                rolePermissionsRepository.saveAll(toAssign);
                                toAssign.forEach(rolePermission -> log.info("Assigned permission {} to role {}",
                                                rolePermission.getPermissions().getMaPermissions(), ACCOUNTANT_ROLE));
                        }

                        // Dang nhap cu co the dang giu cache quyen truoc khi bootstrap.
                        permissionsCacheService.evictAllForRoleChange(accountantRole.getId());

                        log.info("=== Accountant Permissions Initialization Complete ({} permissions, {} assigned) ===",
                                        permissionCodes.size(), assignedIds.size());
                };
        }
}
