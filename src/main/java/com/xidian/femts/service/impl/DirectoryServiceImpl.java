package com.xidian.femts.service.impl;

import com.xidian.femts.entity.Directory;
import com.xidian.femts.repository.DirectoryRepository;
import com.xidian.femts.service.DirectoryService;
import com.xidian.femts.service.ManuscriptService;
import com.xidian.femts.vo.DirList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.xidian.femts.utils.ListUtils.string2List;

/**
 * @author LiuHaonan
 * @date 20:08 2020/2/2
 * @email acerola.orion@foxmail.com
 */
@Service
@Slf4j
public class DirectoryServiceImpl implements DirectoryService {

    private final DirectoryRepository directoryRepository;

    private final ManuscriptService manuscriptService;

    public DirectoryServiceImpl(DirectoryRepository directoryRepository, ManuscriptService manuscriptService) {
        this.directoryRepository = directoryRepository;
        this.manuscriptService = manuscriptService;
    }

    @Override
    public DirList listDirectories(Long id) {
        Directory directory = directoryRepository.getById(id);
        if (directory == null) {
            log.warn("[DIR] no directory with such id <id: {}>", id);
            return null;
        }
        // 下属目录列表
        String subs = directory.getSubs();
        // 下属文档列表
        String docs = directory.getDocs();

        // 目录id列表
        List<String> dirIds = string2List(subs);
        // 文档id列表
        List<String> docIds = string2List(docs);

        List<String> directories = new ArrayList<>(docIds.size());
        List<String> manuscripts = new ArrayList<>(dirIds.size());

        dirIds.forEach(ele -> directories.add(findNameById(Long.parseLong(ele))));
        docIds.forEach(ele -> manuscripts.add(manuscriptService.findTitleById(Long.parseLong(ele))));

        return new DirList(id, directories, manuscripts);
    }

    @Override
    @Cacheable(cacheNames = "directory", key = "#id")
    public Directory findById(Long id) {
        return directoryRepository.findById(id).orElseGet(() -> {
            log.warn("[DIR] no directory with such id <id: {}>", id);
            return null;
        });
    }

    @Override
    @Cacheable(cacheNames = "directoryName", key = "#id")
    public String findNameById(Long id) {
        String name = directoryRepository.findNameById(id);
        if (name == null) {
            log.warn("[DIR] no directory with such id <id: {}>", id);
            return null;
        }
        return name;
    }
}
