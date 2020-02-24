package com.xidian.femts.constants;

import lombok.AllArgsConstructor;

/**
 * 查询用户的条件
 *
 * @author LiuHaonan
 * @date 20:03 2020/1/26
 * @email acerola.orion@foxmail.com
 */
@AllArgsConstructor
public enum UserQueryCondition {

    ID("id"),
    USERNAME("username"),
    JOBID("jobid"),
    PHONE("phone"),
    EMAIL("email");

    private String name;

    public String getName() {
        return name;
    }

    /**
     * 判断参数属于哪一种类型（id除外）
     * @param param 将要判断的参数
     * @return 对应的枚举类型
     */
    public static UserQueryCondition judgeParamType(String param) {
        if (param.contains("@")) {
            return EMAIL;
        } else if(!Character.isDigit(param.charAt(0))) {
            return USERNAME;
        } else if (param.length() == 11) {
            return PHONE;
        } else {
            return JOBID;
        }
    }
}
