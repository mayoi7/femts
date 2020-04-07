package com.xidian.femts.service.impl;

import com.xidian.femts.constants.UserQueryCondition;
import com.xidian.femts.entity.User;
import com.xidian.femts.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author LiuHaonan
 * @date 9:35 2020/2/26
 * @email acerola.orion@foxmail.com
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @Test
    void saveUser() {
        byte[][] bytes = new byte[1024*1024][1024*1024];
        int i = 0;
    }

    @Test
    void updateUser() {
        User user = User.builder()
                .id(6L).username("admin").password(null)
                .phone("13679200326")
                .build();
        User updated = userService.findByCondition("6", UserQueryCondition.ID);
//        BeanUtils.copyProperties(user, updated, getNullPropertyNames(user));
        System.out.println("---");
    }

}