package com.xidian.femts.service.impl;

import com.xidian.femts.entity.Directory;
import com.xidian.femts.repository.DirectoryRepository;
import com.xidian.femts.service.DirectoryService;
import com.xidian.femts.service.InternalCacheService;
import com.xidian.femts.service.ManuscriptService;
import com.xidian.femts.vo.DirectoryElement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
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
    public List<DirectoryElement> listPublicDirectories(Long id, Long userId) {
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
    private List<DirectoryElement> listSubDirectories(Directory directory, Long userId) {
        String subs = directory.getSubs();
        // 下属文档列表
        String docs = directory.getDocs();

        // 目录id列表
        List<String> dirIds = string2List(subs);
        // 文档id列表
        List<String> docIds = string2List(docs);

        List<DirectoryElement> directories = new ArrayList<>(docIds.size());
        List<DirectoryElement> manuscripts = new ArrayList<>(dirIds.size() + docIds.size());

        dirIds.forEach(id -> {
            Long dirId = Long.parseLong(id);
            String name = cacheService.findNameByIdIfVisible_Directory(dirId, userId);
            if (name != null) {
                // leaf为false说明非叶节点，为目录结构
                directories.add(new DirectoryElement(dirId, name, false));
            }
        });
        docIds.forEach(id -> {
            Long docId = Long.parseLong(id);
            manuscripts.add(new DirectoryElement(docId, manuscriptService.findTitleById(docId), true));
        });
        directories.addAll(manuscripts);
        return directories;
    }

    @Override
    @Cacheable(cacheNames = "directory", key = "#id")
    public Directory findById(Long id) {
        return directoryRepository.findById(id).orElseGet(() -> {
            log.warn("[DIR] no directory with such id <id: {}>", id);
            return null;
        });
    }

    /**
     * 创建空目录
     * @param parentId 父目录id
     * @param name 目录名
     * @param userId 创建人id
     * @param visible 是否可见
     * @return 目录结构
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Directory createEmptyDirectory(Long parentId, String name, Long userId, boolean visible) {
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Directory createAndAppendDirectory(Long parentId, String name, Long userId, boolean visible) {
        Directory parent = directoryRepository.findById(parentId).orElse(null);
        if (parent == null) {
            log.error("[DIR] parent directory is not found <parent_id: {}>", parentId);
            return null;
        }
        Directory directory = createEmptyDirectory(parentId, name, userId, visible);
        if (directory == null) {
            log.error("[DIR] directory save failed <parent_id: {}, user_id: {}, name: {}>",
                    parentId, userId, name);
            return null;
        }
        String subs = parent.getSubs();
        if (StringUtils.isEmpty(subs)) {
            subs = "" + directory.getId();
        } else {
            subs += "," + directory.getId();
        }
        parent.setSubs(subs);
        directoryRepository.save(parent);
        return directory;
    }

    @Override
    public Directory appendManuscript(Long parentId, Long docId) {
        Directory parent = directoryRepository.findById(parentId).orElse(null);
        if (parent == null) {
            log.error("[DIR] parent directory is not found <parent_id: {}>", parentId);
            return null;
        }
        String docs = parent.getDocs();
        if (StringUtils.isEmpty(docs)) {
            docs = "" + docId;
        } else {
            docs += "," + docId;
        }
        parent.setDocs(docs);
        return directoryRepository.save(parent);
    }
}
