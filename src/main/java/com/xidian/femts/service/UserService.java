package com.xidian.femts.service;

import com.xidian.femts.constants.UserQueryCondition;
import com.xidian.femts.constants.UserState;
import com.xidian.femts.entity.User;

/**
 * @author LiuHaonan
 * @date 10:19 2020/1/23
 * @email acerola.orion@foxmail.com
 */
public interface UserService {

    /**
     * 根据单个条件查询用户对象
     * @param param 查询参数（不能为空）
     * @param condition 查询条件
     * @return 数据表中的数据对象，如果为空表示无数据
     */
    User findByCondition(String param, UserQueryCondition condition);

    /**
     * 根据用户名查询用户权限级别
     * @param username 用户名
     * @return {@link UserState}的code值，如果为空则说明用户名无效
     */
    Integer findStateByUsername(String username);

    /**
     * 将用户保存到数据库中（禁止更新）
     * @param user 将要插入的用户对象
     * @return 插入后的用户数据，会增加id、时间等属性
     */
    User saveUser(User user);

}
