package com.xidian.femts.service;

import com.xidian.femts.entity.Permission;

/**
 * 管理权限级别接口
 *
 * @author LiuHaonan
 * @date 16:05 2020/2/2
 * @email acerola.orion@foxmail.com
 */
public interface PermissionService {

    /**
     * 查询用户权限级别
     * @param userId 用户id
     * @return 权限级别对象，如果为空说明id不存在
     */
    Permission findById(Long userId);
}
