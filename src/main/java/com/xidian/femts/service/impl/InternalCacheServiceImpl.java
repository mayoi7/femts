package com.xidian.femts.service.impl;

import com.xidian.femts.entity.Directory;
import com.xidian.femts.entity.Manuscript;
import com.xidian.femts.repository.ContentRepository;
import com.xidian.femts.repository.DirectoryRepository;
import com.xidian.femts.repository.ManuscriptRepository;
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

    private final ManuscriptRepository manuscriptRepository;

    private final ContentRepository contentRepository;

    public InternalCacheServiceImpl(DirectoryRepository directoryRepository, ManuscriptRepository manuscriptRepository, ContentRepository contentRepository) {
        this.directoryRepository = directoryRepository;
        this.manuscriptRepository = manuscriptRepository;
        this.contentRepository = contentRepository;
    }

    @Override
    @Cacheable(cacheNames = "directory", key = "#id")
    public Directory findById_Directory(Long id) {
        return directoryRepository.findById(id).orElseGet(() -> {
            log.warn("[DIR] no directory with such id <id: {}>", id);
            return null;
        });
    }

    @Override
    public String findNameById_Directory(Long id) {
        return directoryRepository.findNameById(id);
    }

    @Cacheable(cacheNames = "directoryNameVisible", key = "#id + '$' + #userId")
    public String findNameByIdIfVisible_Directory(Long id, Long userId) {
        return directoryRepository.findNameByIdIfVisible(id, userId);
    }

    @Override
//    @Cacheable(cacheNames = "doc", key = "#id")
    public Manuscript findById_Manuscript(Long id) {
        return manuscriptRepository.findById(id).orElseGet(() -> {
            log.warn("[DOC] found manuscript is null <id: {}>", id);
            return null;
        });
    }

    @Override
//    @Cacheable(cacheNames = "content", key = "#contentId")
    public String findContentById_Content(Long contentId) {
        return contentRepository.findContentById(contentId);
    }
}
