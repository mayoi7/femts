package com.xidian.femts.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * hash方法
 *
 * @author LiuHaonan
 * @date 22:10 2020/2/6
 * @email acerola.orion@foxmail.com
 */
@AllArgsConstructor
@Getter
public enum HashAlgorithm {

    MD5("MD5"),
    SHA("SHA"),
    SHA_256("SHA-256"),
    SHA_512("SHA_512");

    String name;
}
