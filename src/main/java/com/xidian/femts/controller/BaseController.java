package com.xidian.femts.controller;

import com.xidian.femts.vo.ResultVO;
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

    @GetMapping("index")
    public String index() {
        return "/index";
    }

    @GetMapping("register")
    public String register() {
        return "/register";
    }

    @GetMapping("login")
    public String login() {
        return "/login";
    }

    @GetMapping("health")
    @ResponseBody
    public ResultVO health() {
        return new ResultVO("success");
    }

    @GetMapping
    public String passwordReset() {
        return "/reset-password";
    }
}
