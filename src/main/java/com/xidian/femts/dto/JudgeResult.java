package com.xidian.femts.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 表示判断结果，有一个布尔值和一个数据<br/>
 * 为什么不用data是否非空来表示true/false：因为大多数情况下根据判断结果的不同，返回数据也不同<br/>
 * 为什么不设置泛型：因为返回值的泛型无法确定
 *
 * @author LiuHaonan
 * @date 10:06 2020/2/10
 * @email acerola.orion@foxmail.com
 */
@Getter
@AllArgsConstructor
public class JudgeResult<T> {

    public static JudgeResult TRUE = new JudgeResult<>(true, null);

    public static JudgeResult FALSE = new JudgeResult<>(false, null);

    /** true：true */
    private boolean isTrue;

    private T data;

    @Override
    public String toString() {
        return "{isTrue:" + isTrue + ",data:" + data.toString() + "}";
    }
}
