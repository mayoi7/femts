package com.xidian.femts.controller;

import com.xidian.femts.constants.RedisKeys;
import com.xidian.femts.constants.UserQueryCondition;
import com.xidian.femts.constants.UserState;
import com.xidian.femts.entity.User;
import com.xidian.femts.exception.ParamException;
import com.xidian.femts.service.ManuscriptService;
import com.xidian.femts.service.RedisService;
import com.xidian.femts.service.UserService;
import com.xidian.femts.service.impl.EmailService;
import com.xidian.femts.shiro.ShiroSessionListener;
import com.xidian.femts.utils.TokenUtils;
import com.xidian.femts.vo.ResultVO;
import com.xidian.femts.vo.SystemCount;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

import static com.xidian.femts.constants.RedisKeys.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

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

    private final RedisService redisService;

    private final EmailService emailService;

    private final ManuscriptService manuscriptService;

    private final ShiroSessionListener sessionListener;

    public UserController(UserService userService, RedisService redisService, EmailService emailService, @Lazy ShiroSessionListener sessionListener, ManuscriptService manuscriptService) {
        this.userService = userService;
        this.redisService = redisService;
        this.emailService = emailService;
        this.sessionListener = sessionListener;
        this.manuscriptService = manuscriptService;
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

    /**
     * 发送重置密码邮件</br>
     * 会根据用户名和随机数生成一段hash码，作为重置密码的唯一凭证，存储到缓存中
     * @param email 邮箱
     * @return code为200说明请求重置成功，否则说明未发送重置邮件，请重试
     */
    @PostMapping("/password/reset")
    public ModelAndView requestResetPassword(@RequestParam("email") String email) {
        User user = userService.findByCondition(email, UserQueryCondition.EMAIL);
        if (user == null) {
            log.error("[USER] user not found <username: {}>", email);
            throw new ParamException("邮箱不存在");
        }
        String username = user.getUsername();
        // 生成一段随机数，作为重置密码的唯一凭证，并将凭证存储到缓存中设置10分钟过期
        String code = TokenUtils.generateSingleMark();
        // 不同用户的缓存key不同（因为不同用户的过期时间不同）
        redisService.set(RedisKeys.PASSWORD_RESET_KEY + username, code, 60*10);
        emailService.sendResetPasswordMail(email, "/api/1.0/user/password/redirect?code=" + code
                + "&username=" + username);
        return new ModelAndView("redirect:/login");
    }

    /**
     * 跳转到重置密码页面，将缓存中的凭证转移到session中，通过session来进行校验
     * （但是缓存的清除不在这一步）
     * @param code 校验凭证，需要和缓存中做对比
     * @param username 用户名
     * @return 重定向到新页面
     */
    @GetMapping("/password/redirect")
    public ModelAndView redirectPasswordResetPage(@RequestParam("code") String code,
                                                  @RequestParam("username") String username,
                                                  HttpSession session) {
        // 获取缓存中的凭证
        String cacheCode = (String) redisService.get(RedisKeys.PASSWORD_RESET_KEY + username);
        if (!code.equals(cacheCode)) {
            // 对比不一致直接跳到错误页
            log.error("[PASSWORD] code contrast inconsistency <cache: {}, url: {}>", cacheCode, code);
            throw new ParamException(code);
        }
        // 拿缓存key作为session key
        session.setAttribute(RedisKeys.PASSWORD_RESET_KEY, username);
        return new ModelAndView("redirect:/password/reset");
    }

    @PostMapping("password")
    public ResultVO resetPassword(@RequestParam("pwd") String password, HttpSession session) {
        // 获取session中的数据，既验证用户资格，又拿到用户身份
        String username = (String) session.getAttribute(RedisKeys.PASSWORD_RESET_KEY);
        if (username == null) {
            log.error("[PASSWORD] credential is not found in session, request user not have permission");
            return new ResultVO(BAD_REQUEST, "用户非本人");
        }
        // 再检测一遍缓存中是否有凭证，判断是否过期
        Object credential = redisService.get(RedisKeys.PASSWORD_RESET_KEY + username);
        if (credential == null) {
            // 缓存中无凭证说明过期
            log.warn("[PASSWORD] password reset credential has expired <username: {}>", username);
            return new ResultVO(BAD_REQUEST, "凭证过期");
        }
        String encrypted = TokenUtils.encryptPassword(password, username);
        // 不用非空判断，因为如果缓存中有数据，说明用户名是可用的
        User user = userService.findByCondition(username, UserQueryCondition.USERNAME);
        user.setPassword(encrypted);
        if (userService.updateUser(user.getId(), user) == null) {
            log.error("[USER] user updated failed <user: {}>", user);
            return new ResultVO(INTERNAL_SERVER_ERROR, "密码重置失败，请重试或联系管理员");
        } else {
            return ResultVO.SUCCESS;
        }
    }

    /**
     * 修改用户状态，包含赋权以及锁定功能
     * @param userId 用户id
     * @param state 更新后的用户状态
     * @return 返回成功与否的提示信息
     */
    @PostMapping("/state/{userId}")
    @RequiresRoles("sp_admin")
    public ResultVO modifyUserState(@PathVariable Long userId, @RequestBody UserState state) {
        if (state == UserState.SUPER_ADMIN) {
            // 获取当前登陆用户信息及ip
            String authorizer = TokenUtils.getLoggedUserInfo();
            String ip = TokenUtils.getLoggedUserInfo();
            log.error("[USER] user try to authorize super admin, blocked " +
                    "<authorizer_name: {}, authorizer_ip: {}, authorized_id: {}>", authorizer, ip, userId);
            return new ResultVO(BAD_REQUEST, "禁止授权超级管理员");
        }
        User user = userService.findByCondition(userId.toString(), UserQueryCondition.ID);
        if (user == null) {
            log.error("[USER] user id is not existed <user_id: {}>", userId);
            return new ResultVO(BAD_REQUEST, "用户id不存在");
        }
        if (user.getState() == state) {
            return new ResultVO("重复更改");
        }
        user.setState(state);
        user = userService.saveUser(user);
        return new ResultVO(user);
    }

    /**
     * 给用户授权超级管理员<br/>
     * 开发者接口，所有访问该接口的用户，个人信息都会被强制记录
     * @param userId 被授权用户id
     * @return 返回被授权用户信息
     */
    @PostMapping("/authorize/superadmin/{userId}")
    @RequiresRoles("sp_admin")
    public ResultVO authorizeSuperAdmin(@PathVariable Long userId) {
        // 获取当前登陆用户信息及ip
        String authorizer = TokenUtils.getLoggedUserInfo();
        String ip = TokenUtils.getLoggedUserInfo();
        log.warn("[USER] user try to authorize super admin " +
                "<authorizer_name: {}, authorizer_ip: {}, authorized_id: {}>", authorizer, ip, userId);

        User user = userService.findByCondition(userId.toString(), UserQueryCondition.ID);
        if (user == null) {
            log.error("[USER] user id is not existed <user_id: {}>", userId);
            return new ResultVO(BAD_REQUEST, "用户id不存在");
        }
        user.setState(UserState.SUPER_ADMIN);
        user = userService.saveUser(user);
        return new ResultVO(user);
    }

    /**
     * 统计系统数据，包含注册、激活、在线人数以及文档总数
     * @return 返回统计结果
     */
    @GetMapping("count")
    public ResultVO countSystemData() {
        long registered = redisService.count(REGIST_COUNT_KEY);
        if (registered == 0L) {
            registered = userService.countRegistered();
            log.warn("[REDIS] cache lost, reset new value <new_val(registered): {}>", registered);
            redisService.initCounter(REGIST_COUNT_KEY, registered);
        }

        long actived = redisService.count(ACTIVED_COUNT_KEY);
        if (actived == 0L) {
            actived = userService.countActived();
            log.warn("[REDIS] cache lost, reset new value <new_val(actived): {}>", actived);
            redisService.initCounter(ACTIVED_COUNT_KEY, actived);
        }

        long online = redisService.count(ONLINE_COUNT_KEY);
        if (online == 0L) {
            online = sessionListener.getSessionCount();
            log.warn("[REDIS] cache lost, reset new value <new_val(online): {}>", online);
            redisService.initCounter(ONLINE_COUNT_KEY, online);
        }

        long document = redisService.count(DOCUMENT_COUNT_KEY);
        if (document == 0L) {
            document = manuscriptService.countManuscript();
            log.warn("[REDIS] cache lost, reset new value <new_val(document): {}>", document);
            redisService.initCounter(DOCUMENT_COUNT_KEY, document);
        }

        return new ResultVO(new SystemCount(registered, actived, online, document));
    }
}
