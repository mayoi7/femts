package com.xidian.femts.service;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author LiuHaonan
 * @date 20:12 2020/3/24
 * @email acerola.orion@foxmail.com
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
class RedisServiceTest {

    @Autowired
    private RedisService redisService;

    @Test
    void count() {
        System.out.println("ct=" + redisService.count("aaa"));
        System.out.println("ct=" + redisService.incrementAndGet("aaa"));
        System.out.println("ct=" + redisService.incrementAndGet("aaa"));
        System.out.println("ct=" + redisService.decrementAndGet("aaa"));
        System.out.println("ct=" + redisService.count("aaa"));
    }

    @Test
    void incrementAndGet() {
    }

    @Test
    void decrementAndGet() {
    }
}