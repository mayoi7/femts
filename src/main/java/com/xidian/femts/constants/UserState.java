package com.xidian.femts.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 认证级别<br/>
 * 和权限级别不同，权限级别为部门的上下级之间关系，认证级别为管理员和非管理员之间的关系
 *
 * @author LiuHaonan
 * @date 10:28 2020/1/23
 * @email acerola.orion@foxmail.com
 */
@Getter
@AllArgsConstructor
public enum UserState implements CodeEnum {

    LOCKED("locked", "被锁定", 0),
    INACTIVATED("INACTIVATED", "未激活", 1),
    GENERAL("normal", "普通用户", 2),
    ADMIN("admin", "管理员", 3),
    SUPER_ADMIN("sp_admin", "超级管理员", 4);

    private String name;

    private String alias;

    private int code;
}
