package com.xidian.femts.entity;

import com.xidian.femts.other.TestHelper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * @author LiuHaonan
 * @date 21:33 2020/1/30
 * @email acerola.orion@foxmail.com
 */
@Slf4j
class UserTest {

    @Test
    void testUserBuilder() {
        User user = User.builder()
                .username("aaa").password("123").email("aa@qq.com")
                .phone("123456").jobId(111111L).build();
        System.out.println(user.toString());
    }

    @Test
    void testUserLog() {
        User allDataUser = TestHelper.makeUserTemp_AllData();
        User partDataUser = TestHelper.makeUserTemp_NoIdAndTime();

        log.info("<allDataUser: {}>", allDataUser);
        log.info("<partDataUser: {}>", partDataUser);
    }
}