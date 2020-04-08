package com.xidian.femts.controller;

import com.xidian.femts.vo.ResultVO;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 提供一些基础服务
 *
 * @author LiuHaonan
 * @date 16:15 2020/1/18
 * @email acerola.orion@foxmail.com
 */
@Controller
@RequestMapping("")
public class BaseController {

    @GetMapping("/")
    public String redirect() {
        return "index";
    }

    @GetMapping("index")
    public String index() {
        return "/index";
    }

    @GetMapping("admin")
    @RequiresRoles("admin")
    public String admin() {
        return "/admin";
    }

    @GetMapping("error")
    public String error() {
        return "/error";
    }

    @GetMapping("register")
    public String register() {
        return "/register";
    }

    @GetMapping("login")
    public String login() {
        return "/login";
    }

    @GetMapping("forgot")
    public String forgot() {
        return "/forgot-password";
    }

    @GetMapping("/reset/password")
    public String resetPassword() {
        return "reset-password";
    }

    @GetMapping("health")
    @ResponseBody
    public ResultVO health() {
        return new ResultVO("success");
    }

    @GetMapping("test")
    public String test() {
        return "/test";
    }

    @GetMapping
    public String passwordReset() {
        return "/reset-password";
    }
}
