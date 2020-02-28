package com.xidian.femts.other;

import com.xidian.femts.constants.UserState;
import com.xidian.femts.entity.User;

import java.util.Date;

/**
 * 用于辅助测试的工具类
 *
 * @author LiuHaonan
 * @date 15:29 2020/1/31
 * @email acerola.orion@foxmail.com
 */
public class TestHelper {

    public static User makeUserTemp_AllData() {
        Date now = new Date();
        return User.builder()
                .id(5L).createdAt(now).modifiedAt(now)
                .username("aaaaaa").password("123456").jobId(10000000L)
                .phone("13039277142").email("demo@test.com").state(UserState.GENERAL)
                .build();
    }

    public static User makeUserTemp_NoIdAndTime() {
        return User.builder()
                .username("aaaaaa").password("123456").jobId(10000000L)
                .phone("13039277142").email("demo@test.com").state(UserState.GENERAL)
                .build();
    }

}
