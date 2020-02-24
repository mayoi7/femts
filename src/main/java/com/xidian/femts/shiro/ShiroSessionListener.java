package com.xidian.femts.shiro;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 登陆人数监听器
 *
 * @author LiuHaonan
 * @date 20:35 2020/1/23
 * @email acerola.orion@foxmail.com
 */
public class ShiroSessionListener implements SessionListener {

    /**
     * 统计在线人数
     */
    private final AtomicInteger counter = new AtomicInteger(0);

    /**
     * 会话创建时触发
     */
    @Override
    public void onStart(Session session) {
        //会话创建，在线人数加一
        counter.incrementAndGet();
    }

    /**
     * 退出会话时触发
     */
    @Override
    public void onStop(Session session) {
        //会话退出,在线人数减一
        counter.decrementAndGet();
    }

    /**
     * 会话过期时触发
     */
    @Override
    public void onExpiration(Session session) {
        //会话过期,在线人数减一
        counter.decrementAndGet();
    }

    /**
     * 获取当前在线人数
     *
     * @return 当前会话总数
     */
    public int getSessionCount() {
        return counter.get();
    }
}
