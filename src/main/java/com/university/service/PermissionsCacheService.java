package com.university.service;

import com.university.repository.admin.UsersAdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionsCacheService {

    private static final String PERMISSIONS_KEY_PREFIX = "user_permissions:";
    private static final String ROLE_USERS_KEY_PREFIX = "role_users:";
    private static final long DEFAULT_TTL_DAYS = 7;

    private final RedisTemplate<String, Object> redisTemplate;
    private final UsersAdminRepository usersAdminRepository;

    public void cachePermissions(UUID userId, List<String> permissions) {
        String key = PERMISSIONS_KEY_PREFIX + userId;
        redisTemplate.opsForValue().set(key, permissions, DEFAULT_TTL_DAYS, TimeUnit.DAYS);
        log.debug("Cached {} permissions for user {}", permissions.size(), userId);
    }

    @SuppressWarnings("unchecked")
    public List<String> getCachedPermissions(UUID userId) {
        String key = PERMISSIONS_KEY_PREFIX + userId;
        Object value = redisTemplate.opsForValue().get(key);
        if (value instanceof List) {
            return (List<String>) value;
        }
        return null;
    }

    public void evictPermissions(UUID userId) {
        String key = PERMISSIONS_KEY_PREFIX + userId;
        Boolean deleted = redisTemplate.delete(key);
        log.debug("Evicted permissions cache for user {}: {}", userId, deleted);
    }

    public void evictPermissionsForUsersWithRole(UUID roleId) {
        String roleUsersKey = ROLE_USERS_KEY_PREFIX + roleId;
        Object cachedUserIds = redisTemplate.opsForValue().get(roleUsersKey);

        if (cachedUserIds instanceof List<?> userIdList) {
            for (Object id : userIdList) {
                evictPermissions(UUID.fromString(id.toString()));
            }
            redisTemplate.delete(roleUsersKey);
            log.info("Evicted permissions cache for {} users with role {}",
                    userIdList.size(), roleId);
        } else {
            log.debug("No cached user list found for role {}, skipping batch eviction", roleId);
        }
    }

    public void evictAllForRoleChange(UUID roleId) {
        List<UUID> affectedUserIds = usersAdminRepository.findUserIdsByRoleId(roleId);
        if (affectedUserIds.isEmpty()) {
            return;
        }

        for (UUID userId : affectedUserIds) {
            evictPermissions(userId);
        }
        log.info("Evicted permissions cache for {} users after role {} permissions changed",
                affectedUserIds.size(), roleId);
    }

    public void evictAllForUserRoleChange(UUID userId) {
        evictPermissions(userId);
        log.debug("Evicted permissions cache for user {} after user-role change", userId);
    }

    /**
     * Load permissions from DB and cache them.
     * Used when cache is empty/missing after a role-permission change.
     */
    @SuppressWarnings("unchecked")
    public List<String> reloadAndCachePermissions(UUID userId) {
        List<String> permissions = usersAdminRepository.findAllRoleAndPermissionsByUserId(userId)
                .stream()
                .map(dto -> dto.getMaPermissions())
                .filter(p -> p != null)
                .distinct()
                .toList();
        cachePermissions(userId, permissions);
        log.info("Reloaded and cached {} permissions for user {}", permissions.size(), userId);
        return permissions;
    }
}
