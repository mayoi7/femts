package com.xidian.femts.vo;

import lombok.Data;
import lombok.Getter;

/**
 * @author LiuHaonan
 * @date 16:02 2020/4/5
 * @email acerola.orion@foxmail.com
 */
@Data
@Getter
public class LoginData {

    private String name;

    private String password;

    private boolean remember;
}
