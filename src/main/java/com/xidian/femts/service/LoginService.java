package com.xidian.femts.service;

import com.xidian.femts.entity.Login;

/**
 * 登陆记录服务
 *
 * @author LiuHaonan
 * @date 19:52 2020/1/28
 * @email acerola.orion@foxmail.com
 */
public interface LoginService {

    /**
     * 添加一条登陆记录
     * @param userId 用户id（不用强制非空）
     * @param ip ip地址（不用强制非空）
     *           ip为空依然可以进行插入，因为我们可以允许一些查询不到ip的极端情况
     * @return 插入后的记录实体
     */
    Login saveLoginUser(Long userId, String ip);
}
