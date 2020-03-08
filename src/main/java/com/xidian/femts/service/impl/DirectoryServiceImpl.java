package com.xidian.femts.service.impl;

import com.xidian.femts.entity.Directory;
import com.xidian.femts.repository.DirectoryRepository;
import com.xidian.femts.service.DirectoryService;
import com.xidian.femts.service.InternalCacheService;
import com.xidian.femts.service.ManuscriptService;
import com.xidian.femts.vo.DirList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

    private final InternalCacheService cacheService;

    public DirectoryServiceImpl(DirectoryRepository directoryRepository, ManuscriptService manuscriptService, InternalCacheService cacheService) {
        this.directoryRepository = directoryRepository;
        this.manuscriptService = manuscriptService;
        this.cacheService = cacheService;
    }

    @Override
    public DirList listPublicDirectories(Long id, Long userId) {
        Directory directory = directoryRepository.getById(id);
        if (directory == null) {
            log.warn("[DIR] no directory with such id <id: {}>", id);
            return null;
        } else if(!directory.getVisible() && !directory.getCreatedBy().equals(userId)) {
            // 如果当前目录不公开，且创建人又非当前登陆用户，则目录不可见
            log.warn("[DIR] user is not authorized to view the current directory [user_id: {}, dir_id: {}]",
                    userId, id);
            return null;
        }
        return listSubDirectories(directory, userId);
    }

    /**
     * 获取某目录下的直接文档及子目录
     * @param directory 文档数据
     * @return 目录结构
     */
    private DirList listSubDirectories(Directory directory, Long userId) {
        String subs = directory.getSubs();
        // 下属文档列表
        String docs = directory.getDocs();

        // 目录id列表
        List<String> dirIds = string2List(subs);
        // 文档id列表
        List<String> docIds = string2List(docs);

        List<String> directories = new ArrayList<>(docIds.size());
        List<String> manuscripts = new ArrayList<>(dirIds.size());

        dirIds.forEach(ele -> {
            String name = cacheService.findNameByIdIfVisible_Directory(Long.parseLong(ele), userId);
            if (name != null) {
                directories.add(name);
            }
        });
        docIds.forEach(ele -> manuscripts.add(manuscriptService.findTitleById(Long.parseLong(ele))));
        return new DirList(directory.getId(), directories, manuscripts);
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
    public Directory createEmptyDirectory(String name, Long parentId, Long userId, boolean visible) {
        if (StringUtils.isEmpty(name) || parentId == null || userId == null) {
            log.error("[DIR] params should not be null <name: {}, parentId: {}, userId: {}>",
                    name, parentId, userId);
            return null;
        }
        Directory directory = Directory.builder()
                .name(name).parent(parentId).visible(visible).createdBy(userId)
                .build();
        return directoryRepository.save(directory);
    }
}
