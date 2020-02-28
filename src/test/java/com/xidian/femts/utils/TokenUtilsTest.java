package com.xidian.femts.utils;

import com.xidian.femts.entity.User;
import com.xidian.femts.other.TestHelper;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author LiuHaonan
 * @date 15:33 2020/1/14
 * @email acerola.orion@foxmail.com
 */
class TokenUtilsTest {

    @Test
    void generateToken() {
        String token = TokenUtils.generateToken("123456", "admin").toString();
        System.out.println(token);
    }

    @Test
    void generateSingleMark() {
        String uuid = TokenUtils.generateSingleMark();
        System.out.println(uuid);
    }

    @Test
    void generateDigitalSignature() {
        String ds = TokenUtils.generateDigitalSignature("aaaaaa", 1L, "123");
        System.out.println(ds);
    }

    @Test
    void testActivationCodeMakerAndCompare() {
        User user = TestHelper.makeUserTemp_AllData();
        String rawCode = TokenUtils.generateUserActivationCode(user);
        System.out.println("raw_code: " + rawCode);
        boolean isSame = TokenUtils.compareActivationCode(rawCode, user);
        assertTrue(isSame);
    }
}