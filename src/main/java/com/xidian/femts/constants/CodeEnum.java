package com.xidian.femts.constants;

/**
 * 用于其他的枚举类，使其有用于工具类遍历的getCode方法
 *
 * @author LiuHaonan
 * @date 11:16 2020/1/23
 * @email acerola.orion@foxmail.com
 */
public interface CodeEnum {

    /**
     * 获取code值，用于EnumUtil遍历
     *
     * @return 信息码
     */
    int getCode();
}
