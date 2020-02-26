package com.xidian.femts.utils;

import com.xidian.femts.entity.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;

import java.util.UUID;

/**
 * 生成凭证的工具类
 *
 * @author LiuHaonan
 * @date 15:24 2020/1/14
 * @email acerola.orion@foxmail.com
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class TokenUtils {
    /**
     * 加密方式
     */
    private final static String HASH_ALGORITHM = "md5";

    /**
     * 散列次数
     */
    private final static int HASH_TIMES = 1;

    /**
     * 进行凭证的加密
     *
     * @param credential 待加密的凭证
     * @param salt       盐值
     * @return 返回加密后的对象
     */
    public static SimpleHash generateToken(String credential, String salt) {
        return new SimpleHash(HASH_ALGORITHM, credential, salt, HASH_TIMES);
    }

    /**
     * 加密明文密码
     * @param password 密码（明文）
     * @param username 用户名（盐值）
     * @return 加密后的密码
     */
    public static String encryptPassword(String password, String username) {
        return generateToken(password, username).toString();
    }

    /**
     * 生成UUID
     *
     * @return UUID
     */
    public static String generateSingleMark() {
        return UUID.randomUUID().toString();
    }

    /**
     * 生成数字签名
     *
     * @param username   用户名
     * @param expireDate 过期时间（当前时间 + 链接有效时间）
     * @param secretKey  密钥（随机字符串）
     * @return 加密后的数字签名
     */
    public static String generateDigitalSignature(String username, Long expireDate, String secretKey) {
        String sid = username + "$" + expireDate + "$" + secretKey;
        return new SimpleHash(HASH_ALGORITHM, sid, null, HASH_TIMES).toString();
    }

    /**
     * 根据用户信息生成激活码
     * @param user 需要id和username不为空
     * @return 返回字符串形式的激活码
     */
    public static String generateUserActivationCode(User user) {
        if (user == null || user.getId() == null || user.getUsername() == null) {
            log.warn("[Token] user data missing parts of activation code required <user: {}>", user);
            return null;
        }
        return new SimpleHash(HASH_ALGORITHM, user.getUsername(), user.getId().toString(), HASH_TIMES)
                .toString();
    }

    /**
     * 比对激活码是否一致
     * @param code 给出的激活码
     * @param user 激活码信息载体
     * @return true：比对一致；false：不一致
     */
    public static boolean compareActivationCode(String code, User user) {
        String rightCode = generateUserActivationCode(user);
        if (rightCode == null) {
            return false;
        } else {
            return rightCode.equals(code);
        }
    }

    /**
     * 返回已登录用户的登陆信息（用户名）
     * @return 如果不为空，则表示已登陆用户的用户名，如果为空说明当前用户未登陆
     */
    public static String getLoggedUserInfo() {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated() && !subject.isRemembered()) {
            return null;
        } else {
            return subject.getPrincipal().toString();
        }
    }
}
