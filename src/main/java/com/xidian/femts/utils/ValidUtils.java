package com.xidian.femts.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

/**
 * @author LiuHaonan
 * @date 21:45 2020/1/30
 * @email acerola.orion@foxmail.com
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidUtils {

    /** 用户名（汉字+数字的组合）*/
    private static final Pattern REG_USERNAME;

    private static final Pattern REG_PHONE;

    private static final Pattern REG_PASSWORD;

    static {
        REG_USERNAME = Pattern.compile("^[\\u4e00-\\u9fa5]{2,6}\\d{1,3}$");
        REG_PHONE = Pattern.compile("^1[3456789]\\d{9}$");
        REG_PASSWORD = Pattern.compile("^[a-zA-Z0-9!@#$%^&*]{6,20}$");
    }

    private static boolean validByRegex(String param, Pattern regex) {
        return regex.matcher(param).matches();
    }

    /**
     * 验证用户名的合法性
     * @param username 匹配的字符串
     * @return true：验证通过；false：验证不通过
     */
    public static boolean validUsername(String username) {
        return validByRegex(username, REG_USERNAME);
    }

    /**
     * 验证手机号的合法性
     * @param phone 匹配的字符串
     * @return true：验证通过；false：验证不通过
     */
    public static boolean validPhone(String phone) {
        return validByRegex(phone, REG_PHONE);
    }

    /**
     * 验证密码的合法性
     * @param password 匹配的字符串
     * @return true：验证通过；false：验证不通过
     */
    public static boolean validPassword(String password) {
        if (StringUtils.isEmpty(password)) {
            return false;
        }
        return validByRegex(password, REG_PASSWORD);
    }
}
