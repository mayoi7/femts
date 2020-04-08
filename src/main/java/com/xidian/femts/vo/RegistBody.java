package com.xidian.femts.vo;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * @author LiuHaonan
 * @date 9:22 2020/4/8
 * @email acerola.orion@foxmail.com
 */
@Data
public class RegistBody {
    @NotBlank
    private String username;

    @NotBlank
    private Long jobId;

    @NotBlank
    private String password;

    @NotBlank
    private String phone;

    @NotBlank
    @Email(regexp = "^(\\w-*\\.*)+@(\\w-?)+(\\.\\w{2,})+$")
    private String email;
}
