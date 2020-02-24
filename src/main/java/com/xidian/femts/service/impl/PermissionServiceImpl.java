package com.xidian.femts.service.impl;

import com.xidian.femts.entity.Permission;
import com.xidian.femts.repository.PermissionRepository;
import com.xidian.femts.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @author LiuHaonan
 * @date 16:10 2020/2/2
 * @email acerola.orion@foxmail.com
 */
@Service
@Slf4j
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionServiceImpl(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    @Cacheable(cacheNames = "permission", key = "#userId")
    public Permission findById(Long userId) {
        if (userId == null) {
            return null;
        }
        return permissionRepository.findById(userId).orElseGet(() -> {
            log.warn("[PERMISSION] found permission is null <user_id: {}>", userId);
            return null;
        });
    }
}
