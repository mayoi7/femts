package com.xidian.femts.config;

import com.xidian.femts.shiro.ConcurrentLoginControlFilter;
import com.xidian.femts.shiro.CustomRealm;
import com.xidian.femts.shiro.ShiroSessionListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.SessionListener;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.session.mgt.eis.JavaUuidSessionIdGenerator;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.session.mgt.eis.SessionIdGenerator;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author LiuHaonan
 * @date 10:16 2020/1/23
 * @email acerola.orion@foxmail.com
 */
@Configuration
@Slf4j
public class ShiroConfig {

    @Bean
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        // 必须设置 SecurityManager
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        // setLoginUrl 如果不设置值，默认会自动寻找Web工程根目录下的"/login.jsp"页面 或 "/login" 映射
//        shiroFilterFactoryBean.setLoginUrl("/api/1.0/auth/intercept");
        shiroFilterFactoryBean.setLoginUrl("/index");
        // 设置无权限时跳转的 url;
        shiroFilterFactoryBean.setUnauthorizedUrl("");

        //自定义拦截器限制并发人数,参考博客
        LinkedHashMap<String, Filter> filtersMap = new LinkedHashMap<>();
        //限制同一帐号同时在线的个数
        filtersMap.put("clc", concurrentLoginControlFilter());
        shiroFilterFactoryBean.setFilters(filtersMap);

        // 设置拦截器
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
/*
        // 健康检查接口公开
        filterChainDefinitionMap.put("/health/**", "anon");

        // 测试接口公开
        filterChainDefinitionMap.put("/api/1.0/test/**", "anon");

        // 公用api接口公开
        filterChainDefinitionMap.put("/api/1.0/open/**", "anon");

        // 用户，需要记住我或认证
        filterChainDefinitionMap.put("/api/1.0/user/**", "clc, user");
        // 管理员，需要角色权限 “admin”且已登录
        filterChainDefinitionMap.put("/api/1.0/admin/**", "clc, authc, roles[admin]");

        // 开放资源路径
        filterChainDefinitionMap.put("/css/**", "anon");
        filterChainDefinitionMap.put("/js/**", "anon");
        filterChainDefinitionMap.put("/vendor/**", "anon");

        // 开放登陆及注册接口
        filterChainDefinitionMap.put("/api/1.0/auth/**", "clc, anon");

        // 其余接口一律拦截
        // 主要这行代码必须放在所有权限设置的最后，不然会导致所有 url 都被拦截
        filterChainDefinitionMap.put("/**", "clc, user");
*/

        // 测试阶段开放所有接口
        filterChainDefinitionMap.put("/**", "anon");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        log.info("[Configuration] shiro filter factory has injected");
        return shiroFilterFactoryBean;
    }

    /**
     * cookie对象;会话Cookie模板 ,默认为: JSESSIONID
     * 问题: 与SERVLET容器名冲突,重新定义为sid或rememberMe，自定义
     *
     * @return Cookie对象
     */
    @Bean
    public SimpleCookie rememberMeCookie() {
        //这个参数是cookie的名称，对应前端的checkbox的name = rememberMe
        SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
        //setcookie的httponly属性如果设为true的话，会增加对xss防护的安全系数。它有以下特点：

        //setcookie()的第七个参数
        //设为true后，只能通过http访问，javascript无法访问
        //防止xss读取cookie
        simpleCookie.setHttpOnly(true);
        simpleCookie.setPath("/");
        //<!-- 记住我cookie生效时间30天 ,单位秒;-->
        simpleCookie.setMaxAge(2592000);
        return simpleCookie;
    }

    /**
     * cookie管理对象;记住我功能
     *
     * @return rememberMe管理器
     */
    @Bean
    public CookieRememberMeManager rememberMeManager() {
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(rememberMeCookie());
        //rememberMe cookie加密的密钥 建议每个项目都不一样 默认AES算法 密钥长度(128 256 512 位)
        cookieRememberMeManager.setCipherKey(Base64.decode("4AvVhmFLUs0KTA3Kprsdag=="));
        return cookieRememberMeManager;
    }

    /**
     * FormAuthenticationFilter 过滤器 过滤记住我
     *
     * @return 认证过滤器
     */
    @Bean
    public FormAuthenticationFilter formAuthenticationFilter() {
        FormAuthenticationFilter formAuthenticationFilter = new FormAuthenticationFilter();
        //对应前端的checkbox的name = rememberMe
        formAuthenticationFilter.setRememberMeParam("rememberMe");
        return formAuthenticationFilter;
    }

    /**
     * 注入 securityManager
     */
    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        // 设置realm.
        securityManager.setRealm(customRealm());

        //配置记住我 参考博客：
        securityManager.setRememberMeManager(rememberMeManager());

        //配置ehcache缓存管理器 参考博客：
        securityManager.setCacheManager(ehCacheManager());

        // 配置自定义session管理，使用ehcache或者redis
        securityManager.setSessionManager(sessionManager());

        return securityManager;
    }

    /**
     * 凭证匹配器
     * 密码校验交给Shiro的SimpleAuthenticationInfo进行处理
     *
     * @return 哈希认证器
     */
    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        // 散列算法:这里使用MD5算法;
        hashedCredentialsMatcher.setHashAlgorithmName("md5");
        // 散列的次数，比如散列两次，相当于 md5(md5(""));
        hashedCredentialsMatcher.setHashIterations(1);
        return hashedCredentialsMatcher;
    }

    /**
     * 自定义身份认证 realm;
     * 必须写这个类，并加上 @Bean 注解，目的是注入 CustomRealm，
     * 否则会影响 CustomRealm类 中其他类的依赖注入
     *
     * @return 认证器
     */
    @Bean
    public CustomRealm customRealm() {
        CustomRealm customRealm = new CustomRealm();
        customRealm.setCachingEnabled(true);

        //启用身份验证缓存，即缓存AuthenticationInfo信息，默认false
        customRealm.setAuthenticationCachingEnabled(true);
        // 缓存AuthenticationInfo信息的缓存名称 在ehcache.xml中有对应缓存的配置
        customRealm.setAuthenticationCacheName("authenticationCache");

        //启用授权缓存，即缓存AuthorizationInfo信息，默认false
        customRealm.setAuthorizationCachingEnabled(true);
        //缓存AuthorizationInfo信息的缓存名称  在ehcache-shiro.xml中有对应缓存的配置
        customRealm.setAuthorizationCacheName("authorizationCache");

        // 使用md5加密
        customRealm.setCredentialsMatcher(hashedCredentialsMatcher());
        return customRealm;
    }

    /**
     * 下面来两个是为了使@RequiresRoles注解生效
     */
    @Bean
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    /**
     * shiro缓存管理器;
     * 需要添加到securityManager中
     *
     * @return EhCacheManager
     */
    @Bean
    public EhCacheManager ehCacheManager() {
        EhCacheManager cacheManager = new EhCacheManager();
        cacheManager.setCacheManagerConfigFile("classpath:shiro-ehcache.xml");
        return cacheManager;
    }

    /**
     * 配置session监听
     *
     * @return ShiroSessionListener
     */
    @Bean("sessionListener")
    public ShiroSessionListener sessionListener() {
        return new ShiroSessionListener();
    }

    /**
     * 配置会话ID生成器
     *
     * @return SessionIdGenerator
     */
    @Bean
    public SessionIdGenerator sessionIdGenerator() {
        return new JavaUuidSessionIdGenerator();
    }

    /**
     * SessionDAO的作用是为Session提供CRUD并进行持久化的一个shiro组件
     * MemorySessionDAO 直接在内存中进行会话维护
     * EnterpriseCacheSessionDAO  提供了缓存功能的会话维护，
     * 默认情况下使用MapCache实现，内部使用ConcurrentHashMap保存缓存的会话。
     *
     * @return SessionDAO
     */
    @Bean
    public SessionDAO sessionDAO() {
        EnterpriseCacheSessionDAO enterpriseCacheSessionDAO = new EnterpriseCacheSessionDAO();
        //使用ehCacheManager
        enterpriseCacheSessionDAO.setCacheManager(ehCacheManager());
        //设置session缓存的名字 默认为 shiro-activeSessionCache
        enterpriseCacheSessionDAO.setActiveSessionsCacheName("shiro-activeSessionCache");
        //sessionId生成器
        enterpriseCacheSessionDAO.setSessionIdGenerator(sessionIdGenerator());
        return enterpriseCacheSessionDAO;
    }

    /**
     * 配置保存sessionId的cookie
     * 注意：这里的cookie 不是上面的记住我 cookie
     * 记住我需要一个cookie session管理 也需要自己的cookie
     */
    @Bean("sessionIdCookie")
    public SimpleCookie sessionIdCookie() {
        //这个参数是cookie的名称
        SimpleCookie simpleCookie = new SimpleCookie("sid");
        //setCookie的httpOnly属性如果设为true的话，会增加对xss防护的安全系数。它有以下特点：

        //setCookie()的第七个参数
        //设为true后，只能通过http访问，javascript无法访问
        //防止xss读取cookie
        simpleCookie.setHttpOnly(true);
        simpleCookie.setPath("/");
        //maxAge=-1表示浏览器关闭时失效此Cookie
        simpleCookie.setMaxAge(-1);
        return simpleCookie;
    }

    /**
     * 配置会话管理器，设定会话超时及保存
     */
    @Bean("sessionManager")
    public SessionManager sessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        Collection<SessionListener> listeners = new ArrayList<SessionListener>();
        //配置监听
        listeners.add(sessionListener());
        sessionManager.setSessionListeners(listeners);
        sessionManager.setSessionIdCookie(sessionIdCookie());
        sessionManager.setSessionDAO(sessionDAO());
        sessionManager.setCacheManager(ehCacheManager());

        // 全局会话超时时间（单位毫秒），默认30分钟
        sessionManager.setGlobalSessionTimeout(1800000);
        // 是否开启删除无效的session对象  默认为true
        sessionManager.setDeleteInvalidSessions(true);
        // 是否开启定时调度器进行检测过期session 默认为true
        sessionManager.setSessionValidationSchedulerEnabled(true);
        // 设置session失效的扫描时间, 清理用户直接关闭浏览器造成的孤立会话 默认为 1个小时
        // 设置该属性 就不需要设置 ExecutorServiceSessionValidationScheduler
        // 底层也是默认自动调用ExecutorServiceSessionValidationScheduler
        sessionManager.setSessionValidationInterval(3600000);

        sessionManager.setSessionIdUrlRewritingEnabled(false);
        return sessionManager;
    }

    /**
     * 并发登录控制
     *
     * @return session过滤器
     */
    @Bean
    public ConcurrentLoginControlFilter concurrentLoginControlFilter() {
        ConcurrentLoginControlFilter clcFilter = new ConcurrentLoginControlFilter();
        //用于根据会话ID，获取会话进行踢出操作的；
        clcFilter.setSessionManager(sessionManager());
        //使用cacheManager获取相应的cache来缓存用户登录的会话；用于保存用户—会话之间的关系的；
        clcFilter.setCacheManager(ehCacheManager());
        //是否踢出后来登录的，默认是false；即后者登录的用户踢出前者登录的用户；
        clcFilter.setRefuseAfterUser(false);
        //同一个用户最大的会话数，默认1；比如2的意思是同一个用户允许最多同时两个人登录；
        clcFilter.setMaxSession(1);
        //被踢出后重定向到的地址；
        clcFilter.setOutUrl("/login?out=1");
        return clcFilter;
    }

}
