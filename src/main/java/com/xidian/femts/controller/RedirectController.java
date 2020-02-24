package com.xidian.femts.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 页面跳转
 *
 * @author LiuHaonan
 * @date 16:27 2020/1/18
 * @email acerola.orion@foxmail.com
 */
@RequestMapping("")
@Controller
public class RedirectController {

    @GetMapping("/")
    public String index() {
        return "index";
    }
}
