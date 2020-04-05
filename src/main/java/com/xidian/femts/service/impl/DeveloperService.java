package com.xidian.femts.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

/**
 * @author LiuHaonan
 * @date 17:15 2020/4/5
 * @email acerola.orion@foxmail.com
 */
@Service
@Slf4j
public class DeveloperService {

    @CacheEvict(value = "doc", allEntries = true)
    public void deleteDocCache() {
        log.warn("[DEV] remove doc cache");
    }

    @CacheEvict(value = "user", allEntries = true)
    public void deleteUserCache() {
        log.warn("[DEV] remove user cache");
    }
}
