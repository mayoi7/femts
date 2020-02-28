package com.xidian.femts.repository;

import com.xidian.femts.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Calendar;
import java.util.Date;

/**
 * @author LiuHaonan
 * @date 9:36 2020/2/26
 * @email acerola.orion@foxmail.com
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSaveUser() {
        User user = userRepository.findById(6L).get();
        System.out.println(user.toString());
        System.out.println("------------");
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new Date());
//        calendar.add(Calendar.DATE, -7);
//        user.setModifiedAt(calendar.getTime());
//        user.setCreatedAt(calendar.getTime());
        User record = userRepository.save(user);
        System.out.println(record.toString());
    }
}