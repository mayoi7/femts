package com.xidian.femts.shiro;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.DefaultSessionKey;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.Serializable;
import java.util.Deque;
import java.util.LinkedList;

/**
 * 并发登陆控制过滤器
 *
 * @author LiuHaonan
 * @date 19:29 2020/1/23
 * @email acerola.orion@foxmail.com
 */
@Slf4j
@Setter
public class ConcurrentLoginControlFilter extends AccessControlFilter {

    /**
     * 踢出后到的地址
     */
    private String outUrl = "/login";

    /**
     * 是否踢出 后登录的 用户 默认踢出之前登录的用户
     */
    private boolean isRefuseAfterUser = false;

    /**
     * 同一个帐号最大会话数 默认1
     */
    private int maxSession = 1;

    private SessionManager sessionManager;
    
    private Cache<String, Deque<Serializable>> cacheManager;

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager.getCache("shiro-activeSessionCache");
    }

    /**
     * 是否允许访问，返回true表示允许
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request,
                                      ServletResponse response, Object mappedValue) {
        return false;
    }

    /**
     * 表示访问拒绝时是否自己处理，如果返回true表示自己不处理且继续拦截器链执行，
     * 返回false表示自己已经处理了（比如重定向到另一个页面）
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        Subject subject = getSubject(request, response);
        if (!subject.isAuthenticated() && !subject.isRemembered()) {
            //如果没有登录，直接进行之后的流程
            return true;
        }

        Session session = subject.getSession();
        // 这里获取的User是实体 因为在自定义ShiroRealm中的doGetAuthenticationInfo方法中
        // new SimpleAuthenticationInfo(user, password, getName());
        // 传的是 User实体 所以这里拿到的也是实体,如果传的是userName 这里拿到的就是userName

        String username = subject.getPrincipal().toString();
        Serializable sessionId = session.getId();

        // 初始化用户的队列放到缓存里
        Deque<Serializable> deque = cacheManager.get(username);
        if (deque == null) {
            deque = new LinkedList<Serializable>();
            cacheManager.put(username, deque);
        }

        // Concurrent Login Control
        String outName = "clc";

        //如果队列里没有此sessionId，且用户没有被踢出；放入队列
        if (!deque.contains(sessionId) && session.getAttribute(outName) == null) {
            deque.push(sessionId);
        }

        //如果队列里的sessionId数超出最大会话数，开始踢人
        try {
            while (deque.size() > maxSession) {
                Serializable clcSessionId = null;
                if (isRefuseAfterUser) {
                    //踢出后登陆者
                    clcSessionId = deque.getFirst();
                    clcSessionId = deque.removeFirst();
                } else {
                    //踢出前登陆者
                    clcSessionId = deque.removeLast();
                }
                Session clcSession = sessionManager.getSession(new DefaultSessionKey(clcSessionId));
                if (clcSession != null) {
                    //设置会话的clc属性表示踢出了
                    clcSession.setAttribute("clc", true);
                }
            }
        } catch (Exception ex) {
            log.error("[CLC] kick out fail", ex);
            ex.printStackTrace();
        }

        //如果被踢出了，直接退出，重定向到踢出后的地址
        if (session.getAttribute(outName) != null) {
            //会话被踢出了
            try {
                subject.logout();
            } catch (Exception ex) {
                // FIXME: sl4j的log和shiro的log冲突
                log.error("[CLC] kick out fail", ex);
                ex.printStackTrace();
            }
            WebUtils.issueRedirect(request, response, outUrl);
            return false;
        }
        return true;
    }
}
