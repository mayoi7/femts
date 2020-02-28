package com.xidian.femts.utils;

import com.xidian.femts.service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author LiuHaonan
 * @date 8:53 2020/1/14
 * @email acerola.orion@foxmail.com
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    /**
     * pass
     */
    @Test
    void sendResetPasswordMail() {
        String to = "974483019@qq.com";
        emailService.sendResetPasswordMail("974483019@qq.com",
                "/login/password/aaaaaa/reset?code=f6ac7570300e8c887ac85ad2e2d4592c");
    }
}