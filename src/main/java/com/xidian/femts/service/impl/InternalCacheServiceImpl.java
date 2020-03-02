package com.xidian.femts.service.impl;

import com.xidian.femts.repository.DirectoryRepository;
import com.xidian.femts.service.InternalCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @author LiuHaonan
 * @date 16:27 2020/3/2
 * @email acerola.orion@foxmail.com
 */
@Service
@Slf4j
public class InternalCacheServiceImpl implements InternalCacheService {

    private final DirectoryRepository directoryRepository;

    public InternalCacheServiceImpl(DirectoryRepository directoryRepository) {
        this.directoryRepository = directoryRepository;
    }

    @Override
    @Cacheable(cacheNames = "directoryName", key = "#id")
    public String findNameById(Long id) {
        return directoryRepository.findNameById(id);
    }

    @Cacheable(cacheNames = "directoryNameVisible", key = "#id + '$' + #userId")
    public String findNameByIdIfVisible(Long id, Long userId) {
        return directoryRepository.findNameByIdIfVisible(id, userId);
    }
}
