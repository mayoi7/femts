package com.xidian.femts.utils;

import com.xidian.femts.constants.HashAlgorithm;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 计算hash值的工具类
 * @author LiuHaonan
 * @date 21:53 2020/2/6
 * @email acerola.orion@foxmail.com
 */
public class HashUtils {

    private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "a", "b", "c", "d", "e", "f" };

    /**
     * 将字节数组转换为16进制的字符串
     *
     * @param bytes 字节数组
     * @return 16进制的字符串
     */
    private static String byteArrayToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte byt : bytes) {
            sb.append(byteToHexString(byt));
        }
        return sb.toString();
    }

    /**
     * 将字节转换为16进制字符串
     *
     * @param b 字节
     * @return 16进制字符串
     */
    private static String byteToHexString(byte b) {
        return hexDigits[(b >> 4) & 0x0f] + hexDigits[b & 0x0f];
    }

    /**
     * 将摘要信息转换为相应的编码
     *
     * @param algorithm hash算法
     * @param message 摘要信息
     * @return 相应的编码字符串
     */
    public static String hashString(HashAlgorithm algorithm, String message) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance(algorithm.getName());
            return byteArrayToHexString(md.digest(message.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将字节数组转换为相应的编码
     *
     * @param algorithm hash算法
     * @param bytes 字节信息数组
     * @return 相应的编码字符串
     */
    public static String hashBytes(HashAlgorithm algorithm, byte[] bytes) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance(algorithm.getName());
            return byteArrayToHexString(md.digest(bytes));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

}
