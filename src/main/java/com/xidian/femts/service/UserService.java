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
     * 根据用户名查询用户id
     * @param username 用户名
     * @return 用户id，如果不存在则返回null
     */
    Long findIdByUsername(String username);

    /**
     * 根据用户id查询用户名
     * @param userId 用户id
     * @return 用户的用户名，如果不存在则返回null
     */
    String findUsernameById(Long userId);

    /**
     * 根据单个条件查询用户对象</br>
     * 因为查询不到数据时往往是调用方参数错误，所以在方法内不打印日志，由调用方自行打印
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

    /**
     * 更新用户信息
     * @param userId 用户id（和user中数据一致，放在参数中仅为提醒调用方注意userId不能为空）
     * @param user 用户信息（将会将该数据<b>覆盖</b>数据表中记录，所以该数据需要调用方先从数据库中查询）
     * @return 返回更新后的用户信息
     */
    User updateUser(Long userId, User user);

    /**
     * 统计注册人数
     * @return 返回当前注册人数
     */
    Long countRegistered();

    /**
     * 统计激活人数
     * @return 返回当前激活人数
     */
    Long countActived();
}
