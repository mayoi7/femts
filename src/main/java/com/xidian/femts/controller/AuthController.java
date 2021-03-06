package com.xidian.femts.controller;

import com.alibaba.fastjson.JSON;
import com.xidian.femts.constants.RedisKeys;
import com.xidian.femts.constants.UserQueryCondition;
import com.xidian.femts.constants.UserState;
import com.xidian.femts.entity.User;
import com.xidian.femts.service.LoginService;
import com.xidian.femts.service.RedisService;
import com.xidian.femts.service.UserService;
import com.xidian.femts.service.impl.EmailService;
import com.xidian.femts.utils.TokenUtils;
import com.xidian.femts.vo.LoginData;
import com.xidian.femts.vo.RegistBody;
import com.xidian.femts.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

import static com.xidian.femts.utils.NetworkUtils.getIpAddr;
import static com.xidian.femts.utils.TokenUtils.compareActivationCode;
import static com.xidian.femts.utils.TokenUtils.generateUserActivationCode;
import static com.xidian.femts.utils.ValidUtils.*;

/**
 * 登陆认证控制
 *
 * @author LiuHaonan
 * @date 11:41 2020/1/26
 * @email acerola.orion@foxmail.com
 */
@RestController
@RequestMapping("/api/1.0/auth")
@Slf4j
public class AuthController {

    private final UserService userService;

    private final LoginService loginService;

    private final EmailService emailService;

    private final RedisService redisService;

    public AuthController(UserService userService, LoginService loginService, EmailService emailService, RedisService redisService) {
        this.userService = userService;
        this.loginService = loginService;
        this.emailService = emailService;
        this.redisService = redisService;
    }

    @GetMapping("/intercept")
    public ResultVO intercept() {
        return new ResultVO(HttpStatus.PERMANENT_REDIRECT, "重定向接口");
    }

    /**
     * 登陆
     * @param loginData 登陆数据
     * @return 如果成功，则返回默认成功信息
     */
    @PostMapping("/login")
    public ResultVO login(@RequestBody LoginData loginData, HttpServletRequest request) {
        String name = loginData.getName();
        String password = loginData.getPassword();
        boolean remember = loginData.isRemember();

        // 添加method自适应判断
        UserQueryCondition method = UserQueryCondition.judgeParamType(name);

        User user = userService.findByCondition(name, method);
        if (user == null) {
            return new ResultVO(HttpStatus.BAD_REQUEST, "帐号不存在");
        } else if(!user.getState().canLogin()) {
            return new ResultVO(HttpStatus.BAD_REQUEST, "帐号未激活或被锁定");
        }
        // userId用于插入登陆记录
        Long userId = user.getId();
        String username = user.getUsername();

        // 从SecurityUtils里边创建一个 subject
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            // 如果未认证，则在认证提交前准备 token（令牌）
            UsernamePasswordToken token = new UsernamePasswordToken(username, password);
            // 保存登陆记录
            if (remember) {
                token.setRememberMe(true);
            }
            // 执行认证登陆
            try {
                subject.login(token);
            } catch (UnknownAccountException uae) {
                log.warn("[Login] username wrong <username: {}>", username);
                return new ResultVO(HttpStatus.BAD_REQUEST,"用户名错误");
            } catch (IncorrectCredentialsException ice) {
                log.warn("[Login] password wrong <username: {}>", username);
                return new ResultVO(HttpStatus.BAD_REQUEST,"密码错误");
            } catch (LockedAccountException lae) {
                log.warn("[Login] user be locked <username: {}>", username);
                return new ResultVO(HttpStatus.BAD_REQUEST,"账号被锁定");
            } catch (Exception ex) {
                log.warn("[Login] unknown exception <username: {}>", username, ex);
                return new ResultVO(HttpStatus.INTERNAL_SERVER_ERROR,"未知认证异常");
            }
        }

        // 插入登陆记录
        String ip = getIpAddr(request);
        loginService.saveLoginUser(userId, ip);

        return ResultVO.SUCCESS;
    }

    /**
     * 注销
     * @return 跳转到登录页
     */
    @GetMapping("/logout")
    public ModelAndView logout() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();

        // 跳转回主页
        return new ModelAndView("redirect:/index");
    }

    /**
     * 用户注册接口
     * @param registBody 注册数据
     * @return {@link ResultVO}通知信息
     */
    @PostMapping("/regist")
    public ResultVO registered(@RequestBody RegistBody registBody) {
        // 先校验参数合法性
        if (!validUsername(registBody.getUsername())
                || !validPassword(registBody.getPassword())
                || !validPhone(registBody.getPhone())) {
            return new ResultVO("参数不合规范，请重新检查");
        }

        // 加密密码
        String encrypted = TokenUtils.encryptPassword(registBody.getPassword(), registBody.getUsername());

        User user = User.builder()
                .username(registBody.getUsername()).password(encrypted)
                .jobId(registBody.getJobId()).phone(registBody.getPhone()).email(registBody.getEmail())
                .build();
        User record = userService.saveUser(user);
        if (record == null) {
            log.error("[User] user registered failed <user: {}>", user);
            return new ResultVO(HttpStatus.INTERNAL_SERVER_ERROR, "注册失败");
        }

        // 发激活邮件
        String url = "/api/1.0/auth/active?id=" + user.getId() + "&code=" + generateUserActivationCode(user);
        emailService.sendActiveMail(record.getEmail(), url);

        // 注册人数自增
        redisService.incrementAndGet(RedisKeys.REGIST_COUNT_KEY);

        return ResultVO.SUCCESS;
    }

    /**
     * 激活刚注册用户
     * @param id 用户id
     * @param code 激活码（根据用户名和id计算出）
     * @return 重定向到首页
     */
    @GetMapping("active")
    public ModelAndView active(@RequestParam("id") String id,
                               @RequestParam("code") String code) {
        User user = userService.findByCondition(id, UserQueryCondition.ID);
        if (user == null) {
            log.error("[User] id no exist <id: {}>", id);
            throw new RuntimeException("id不存在，用户可能手动拼接了激活链接");
        }
        if (!compareActivationCode(code, user)) {
            // 比对失败，没有激活成功
            log.error("[User] active fail <id: {}, active_code: {}> " +
                    "<<users may manually splice the activation url>>", id, code);
            throw new RuntimeException("id不存在，用户可能手动拼接了激活链接");
        }

        user.setState(UserState.GENERAL);
        userService.updateUser(user.getId(), user);

        // 激活人数自增
        redisService.incrementAndGet(RedisKeys.ACTIVED_COUNT_KEY);

        return new ModelAndView("redirect:/index");
    }

    /**
     * 返回当前登录用户信息
     * @return 数据为脱敏的用户信息
     */
    @GetMapping("/user")
    public ResultVO returnLoginUserInfo() {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated() && !subject.isRemembered()) {
            // 用户未登录
            log.warn("[Auth] client request not logged user info");
            return new ResultVO(HttpStatus.BAD_REQUEST, "无法查询未登录用户的信息");
        }
        String username = subject.getPrincipal().toString();
        User user = userService.findByCondition(username, UserQueryCondition.USERNAME);
        // 当第三方将帐号删除时可能导致subject中信息失效，所以要进行判空
        if (user == null) {
            log.error("[Auth] missing current logged user info <username: {}>" +
                    "<<reason: may due to third party operations>>", username);
            return new ResultVO(HttpStatus.INTERNAL_SERVER_ERROR, "登录用户信息丢失");
        }
        // 信息脱敏
        user.setPassword(null);
        return new ResultVO(JSON.toJSONString(user));
    }
}
