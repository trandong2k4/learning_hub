package com.university.config;

import com.university.entity.Permissions;
import com.university.entity.Role;
import com.university.entity.RolePermissions;
import com.university.enums.AdminPermission;
import com.university.repository.admin.PermissionsAdminRepository;
import com.university.repository.admin.RoleAdminRepository;
import com.university.repository.admin.RolePermissionsAdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Initializer de tao permissions mac dinh cho Admin khi ung dung khoi dong.
 * Su dung batch operations de tranh N+1 query.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class AdminPermissionsInitializer {

    private final PermissionsAdminRepository permissionsRepository;
    private final RoleAdminRepository roleRepository;
    private final RolePermissionsAdminRepository rolePermissionsRepository;

    private static final String ADMIN_ROLE = "ADMIN";

    @Bean
    public ApplicationRunner adminPermissionsRunner(
            @Value("${app.permissions.auto-assign:false}") boolean autoAssign) {
        return args -> {

            List<String> permissionCodes = Arrays.stream(AdminPermission.values())
                    .map(Enum::name)
                    .toList();

            // 1. Lay tat ca permission codes hien co trong DB - 1 query
            Set<String> existingCodes = new HashSet<>(permissionsRepository.findAllMaPermissions());

            // 2. Tao nhung permission chua ton tai - 1 query saveAll
            List<Permissions> toSave = Arrays.stream(AdminPermission.values())
                    .filter(p -> !existingCodes.contains(p.name()))
                    .map(p -> {
                        Permissions entity = new Permissions();
                        entity.setMaPermissions(p.name());
                        entity.setMoTa(p.getMoTa());
                        return entity;
                    })
                    .toList();

            if (!toSave.isEmpty()) {
                permissionsRepository.saveAll(toSave);
                toSave.forEach(p -> log.info("Created permission: {} - {}", p.getMaPermissions(), p.getMoTa()));
                existingCodes.addAll(permissionCodes);
            }

            // 3. Bo qua buoc gan role neu khong cho phep
            if (!autoAssign) {
                log.info("PERMISSIONS_AUTO_ASSIGN=false — bo qua gan quyen vao role");
                log.info("=== Admin Permissions Initialization Complete ({} permissions created) ===",
                        permissionCodes.size());
                return;
            }

            // 4. Tim hoac tao role ADMIN - 1 query
            Role adminRole = roleRepository.findFirstByMaRoleIgnoreCase(ADMIN_ROLE)
                    .orElseGet(() -> {
                        Role newRole = new Role();
                        newRole.setMaRole(ADMIN_ROLE);
                        newRole.setMoTa("Quan tri vien");
                        Role saved = roleRepository.save(newRole);
                        log.info("Created role: {}", ADMIN_ROLE);
                        return saved;
                    });

            // 5. Lay tat ca permissions theo codes - 1 query
            List<Permissions> allPermissions = permissionsRepository.findByMaPermissionsIn(permissionCodes);
            Map<String, Permissions> permMap = allPermissions.stream()
                    .collect(Collectors.toMap(Permissions::getMaPermissions, p -> p));

            // 6. Lay permission IDs da gan cho role nay - 1 query
            List<UUID> permIds = allPermissions.stream().map(Permissions::getId).toList();
            Set<UUID> assignedIds = rolePermissionsRepository.findAssignedPermissionsIds(permIds);

            // 7. Tao nhung RolePermissions chua co - 1 query saveAll
            List<RolePermissions> toAssign = Arrays.stream(AdminPermission.values())
                    .filter(p -> !assignedIds.contains(permMap.get(p.name()).getId()))
                    .map(p -> {
                        RolePermissions rp = new RolePermissions();
                        rp.setRole(adminRole);
                        rp.setPermissions(permMap.get(p.name()));
                        return rp;
                    })
                    .toList();

            if (!toAssign.isEmpty()) {
                rolePermissionsRepository.saveAll(toAssign);
                toAssign.forEach(rp -> log.info("Assigned permission {} to role {}",
                        rp.getPermissions().getMaPermissions(), ADMIN_ROLE));
            }

            log.info("=== Admin Permissions Initialization Complete ({} permissions, {} assigned) ===",
                    permissionCodes.size(), assignedIds.size());
        };
    }
}
