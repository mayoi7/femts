package com.xidian.femts.controller;

import com.xidian.femts.constants.UserQueryCondition;
import com.xidian.femts.entity.User;
import com.xidian.femts.service.UserService;
import com.xidian.femts.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * 用户信息控制
 *
 * @author LiuHaonan
 * @date 21:52 2020/1/28
 * @email acerola.orion@foxmail.com
 */
@RestController
@RequestMapping("/api/1.0/user")
@Slf4j
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 用户接口健康测试（主要用于测试权限是否通过）
     * @return 默认成功
     */
    @GetMapping("/health")
    public ResultVO health() {
        return ResultVO.SUCCESS;
    }

    /**
     * 检测用户名是否重复<br/>
     * 注意：不检测用户名长度字符等是否符合规范，这部分交给前端和最终的注册接口，
     * 在这里做这种检测毫无意义且降低效率
     * @param username 待检测的用户名
     * @return 返回json数据，data为具体提示信息
     */
    @GetMapping("/defection/{username}")
    public ResultVO defectUsername(@PathVariable("username") String username) {
        User user = userService.findByCondition(username, UserQueryCondition.USERNAME);
        if (user != null) {
            return new ResultVO(HttpStatus.BAD_REQUEST, "用户名已存在");
        } else {
            return ResultVO.SUCCESS;
        }
    }
}
