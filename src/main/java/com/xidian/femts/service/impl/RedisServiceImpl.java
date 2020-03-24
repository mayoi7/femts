package com.xidian.femts.service.impl;

import com.xidian.femts.service.RedisService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author LiuHaonan
 * @date 22:11 2020/2/24
 * @email acerola.orion@foxmail.com
 */
@Service
public class RedisServiceImpl implements RedisService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void set(String key, Object value) {
//        System.out.println("set - " + key);
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void set(String key, Object value, int seconds) {
        redisTemplate.opsForValue().set(key, value, seconds, TimeUnit.SECONDS);
    }

    @Override
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void del(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public Long count(String key) {
        RedisAtomicLong counter = new RedisAtomicLong(key,
                Objects.requireNonNull(redisTemplate.getConnectionFactory()));
        return counter.get();
    }

    @Override
    public void initCounter(String key, long initVal) {
        RedisAtomicLong counter = new RedisAtomicLong(key,
                Objects.requireNonNull(redisTemplate.getConnectionFactory()));
        counter.set(initVal);
    }

    @Override
    public Long incrementAndGet(String key) {
        RedisAtomicLong counter = new RedisAtomicLong(key,
                Objects.requireNonNull(redisTemplate.getConnectionFactory()));
        return counter.incrementAndGet();
    }

    @Override
    public Long decrementAndGet(String key) {
        RedisAtomicLong counter = new RedisAtomicLong(key,
                Objects.requireNonNull(redisTemplate.getConnectionFactory()));
        return counter.decrementAndGet();
    }

}
