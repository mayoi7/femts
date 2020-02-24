package com.xidian.femts.utils;

import com.xidian.femts.constants.CodeEnum;

/**
 * @author LiuHaonan
 * @date 11:13 2020/1/23
 * @email acerola.orion@foxmail.com
 */
public class EnumUtils {
    /**
     * 查询符合code值的枚举，同时返回该枚举
     *
     * @param code      需要对应字符串表示的码
     * @param enumClass code对应的枚举类
     * @param <T>       枚举对象类型
     * @return 查询到的枚举类型，如果为null表示该码不在枚举类的表示范围内
     */
    public static <T extends CodeEnum> T getByCode(Integer code, Class<T> enumClass) {
        if (code == null) {
            return null;
        }
        /*
         * getEnumConstants()作用：
         * 以声明顺序返回一个数组，该数组包含构成此 Class 对象所表示的枚举类的值，
         * 或者在此 Class 对象不表示枚举类型时返回 null
         */
        for (T each : enumClass.getEnumConstants()) {
            if (each.getCode() == code) {
                return each;
            }
        }
        return null;
    }
}

