package com.xidian.femts.shiro;

import com.xidian.femts.constants.UserQueryCondition;
import com.xidian.femts.constants.UserState;
import com.xidian.femts.entity.User;
import com.xidian.femts.service.UserService;
import com.xidian.femts.utils.EnumUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author LiuHaonan
 * @date 10:18 2020/1/23
 * @email acerola.orion@foxmail.com
 */
@Slf4j
public class CustomRealm extends AuthorizingRealm {
    private UserService userService;

    @Autowired
    @Lazy
    private void setUserMapper(UserService userService) {
        this.userService = userService;
    }

    /**
     * 获取身份验证信息
     * Shiro中，最终是通过 Realm 来获取应用程序中的用户、角色及权限信息的。
     *
     * @param authenticationToken 用户身份信息 token
     * @return 返回封装了用户信息的 AuthenticationInfo 实例
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        // 前台输入的用户名和密码
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        // 从数据库获取对应用户名密码的用户
        User user = userService.findByCondition(token.getUsername(), UserQueryCondition.USERNAME);
        if (user == null) {
            throw new UnknownAccountException();
        }

        // 这里的password是加密后的密码
        String password = user.getPassword();
//        if (!password.equals(new String((char[]) token.getCredentials()))) {
//            throw new IncorrectCredentialsException();
//        }
        //盐值
        ByteSource credentialsSalt = ByteSource.Util.bytes(user.getUsername());

        return new SimpleAuthenticationInfo(token.getPrincipal(), password,
                credentialsSalt, getName());
    }

    /**
     * 获取授权信息
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        //获得该用户角色
        Integer authCode = userService.findStateByUsername(username);
        Set<String> roles = new HashSet<>();

        //需要将 AuthType 封装到 Set 作为 info.setRoles() 的参数
        UserState auth = EnumUtils.getByCode(authCode, UserState.class);

        // 该用户的权限码错误
        if (auth == null) {
            log.error("[Auth] user auth is abnormal <user: {}, auth_code: {}>", username, authCode);
        } else {
            roles.add(auth.getName());
            // 普通用户的权限码
            int normalUserCode = UserState.GENERAL.getCode();

            // 赋予该用户所有低级权限
            for (int i = normalUserCode; i < authCode; i++) {
                roles.add(Objects.requireNonNull(EnumUtils.getByCode(i, UserState.class)).getName());
            }
        }

        //设置该用户拥有的角色
        return new SimpleAuthorizationInfo(roles);
    }

    /**
     * 重写方法,清除当前用户的的 授权缓存
     *
     * @param principals 权限信息
     */
    @Override
    public void clearCachedAuthorizationInfo(PrincipalCollection principals) {
        super.clearCachedAuthorizationInfo(principals);
    }

    /**
     * 重写方法，清除当前用户的 认证缓存
     *
     * @param principals 认证信息
     */
    @Override
    public void clearCachedAuthenticationInfo(PrincipalCollection principals) {
        super.clearCachedAuthenticationInfo(principals);
    }

    @Override
    public void clearCache(PrincipalCollection principals) {
        super.clearCache(principals);
    }

    /**
     * 自定义方法：清除所有 授权缓存
     */
    public void clearAllCachedAuthorizationInfo() {
        getAuthorizationCache().clear();
    }

    /**
     * 自定义方法：清除所有 认证缓存
     */
    public void clearAllCachedAuthenticationInfo() {
        getAuthenticationCache().clear();
    }

    /**
     * 自定义方法：清除所有的认证缓存和授权缓存
     */
    public void clearAllCache() {
        clearAllCachedAuthenticationInfo();
        clearAllCachedAuthorizationInfo();
    }
}
