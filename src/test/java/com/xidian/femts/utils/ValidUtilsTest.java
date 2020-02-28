package com.xidian.femts.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author LiuHaonan
 * @date 22:25 2020/1/30
 * @email acerola.orion@foxmail.com
 */
class ValidUtilsTest {

    @Test
    void validNormalName() {
        String string1 = "asd";
        String string2 = "sadsadasdadsdsadsdsadds";
        String string3 = "asdwrdfxs";
        String string4 = "1234567890";
        String string5 = "a123456";
        String string6 = "sad$#@&_5dg";

        boolean match1 = ValidUtils.validUsername(string1);
        boolean match2 = ValidUtils.validUsername(string2);
        boolean match3 = ValidUtils.validUsername(string3);
        boolean match4 = ValidUtils.validUsername(string4);
        boolean match5 = ValidUtils.validUsername(string5);
        boolean match6 = ValidUtils.validUsername(string6);

        assertFalse(match1);    // asd
        assertFalse(match2);    // sadsadasdadsdsadsdsadds
        assertFalse(match4);    // 1234567890

        assertTrue(match3);     // asdwrdfxs
        assertTrue(match5);     // a123456
        assertTrue(match6);     // sad$#@&_5dg
    }
}