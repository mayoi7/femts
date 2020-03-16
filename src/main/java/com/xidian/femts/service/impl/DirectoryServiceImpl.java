package com.xidian.femts.service.impl;

import com.xidian.femts.entity.Directory;
import com.xidian.femts.entity.Manuscript;
import com.xidian.femts.entity.User;
import com.xidian.femts.repository.DirectoryRepository;
import com.xidian.femts.service.DirectoryService;
import com.xidian.femts.service.InternalCacheService;
import com.xidian.femts.service.ManuscriptService;
import com.xidian.femts.service.UserService;
import com.xidian.femts.utils.ListUtils;
import com.xidian.femts.vo.DirectoryElement;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.xidian.femts.constants.UserQueryCondition.ID;
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

    private final UserService userService;

    private final ManuscriptService manuscriptService;

    private final InternalCacheService cacheService;

    public DirectoryServiceImpl(DirectoryRepository directoryRepository, ManuscriptService manuscriptService, InternalCacheService cacheService, UserService userService) {
        this.directoryRepository = directoryRepository;
        this.manuscriptService = manuscriptService;
        this.cacheService = cacheService;
        this.userService = userService;
    }

    @Override
    public List<DirectoryElement> listVisibleDirectories(Long id, Long userId) {
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
     * @param readerId 浏览人用户id（必须有效）
     * @return 目录结构
     */
    private List<DirectoryElement> listSubDirectories(Directory directory, Long readerId) {
        // 下属目录和文档列表
        String subs = directory.getSubs();
        String docs = directory.getDocs();

        // 目录和文档id列表
        List<String> dirIds = string2List(subs);
        List<String> docIds = string2List(docs);

        List<DirectoryElement> directories = new ArrayList<>(docIds.size());
        List<DirectoryElement> manuscripts = new ArrayList<>(dirIds.size() + docIds.size());

        dirIds.forEach(id -> {
            Long dirId = Long.parseLong(id);
            String name = cacheService.findNameByIdIfVisible_Directory(dirId, readerId);
            if (name != null) {
                // leaf为false说明非叶节点，为目录结构
                directories.add(new DirectoryElement(dirId, name, false));
            }
        });
        docIds.forEach(id -> {
            Long docId = Long.parseLong(id);
            Manuscript manuscript = cacheService.findById_Manuscript(docId);
            if (checkManuscriptVisibility(manuscript, readerId)) {
                // 判断为有权浏览时再添加文档
                manuscripts.add(new DirectoryElement(docId, manuscript.getTitle(), true));
            }
        });
        directories.addAll(manuscripts);
        return directories;
    }

    /**
     * 检测文档是否可以被用户浏览
     * @param manuscript 文档数据
     * @param readerId 读取人id
     * @return true：读取人有权浏览；false：文档对读取人不可见
     */
    private boolean checkManuscriptVisibility(Manuscript manuscript, Long readerId) {
        if (manuscript.getCreatedBy().equals(readerId)) {
            // 读取人即为文档创建者时，无条件授权
            return true;
        } else {
            switch (manuscript.getLevel()) {
                case PUBLIC:
                case UNEDITABLE:
                    // 公开和不可编辑两种状态是对所有人可见的
                    return true;
                case CONFIDENTIAL:
                    // 上级可见需要查询用户权限级别
                    User creator = userService.findByCondition(manuscript.getCreatedBy().toString(), ID);
                    User reader = userService.findByCondition(readerId.toString(), ID);
                    // 读取人权限较大时，则有权浏览
                    return reader.getLevel() > creator.getLevel();
                case PRIVATE:
                default:
                    // 当读取人不为文档创建者时，私有文档对其他人不可见
                    return false;
            }
        }
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
    @CacheEvict(cacheNames = "directory", key = "#parentId")
    @Transactional(rollbackFor = Exception.class)
    public Directory createAndAppendDirectory(Long parentId, String name, Long userId, boolean visible) {
        Directory parent = cacheService.findById_Directory(parentId);
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
            @CacheEvict(cacheNames = "doc", key = "#id"),
            @CacheEvict(cacheNames = "docTitle", key = "#id")
    })
    public void deleteManuscript(Long id, Long directoryId) {
        Directory directory = cacheService.findById_Directory(directoryId);
        if (directory == null) {
            log.error("[DOC] document directory is no longer valid or has been deleted " +
                    "<doc_id: {}, directory_id: {}>", id, directoryId);
            return;
        }
        // 获取id列表
        String docIds = directory.getDocs();
        if (StringUtils.isEmpty(docIds)) {
            // 如果本身就没有数据，直接返回
            log.warn("[DIRECTORY] doc id list is null but need to delete <directory_id: {}>", id);
            return;
        }
        List<String> idList = ListUtils.string2List(docIds);
        Iterator<String> iterator = idList.iterator();
        while(iterator.hasNext()) {
            if (iterator.next().equals(directoryId.toString())) {
                iterator.remove();
                break;
            }
        }
        String deletedIdList = ListUtils.list2String(idList);
        directory.setDocs(deletedIdList);
        directoryRepository.save(directory);
    }

    @Override
    @Caching(put = @CachePut(cacheNames = "directory", key = "#directoryId"),
            // 定向更新所有用户的可见目录不可行，所以选择清除所有缓存，如果测试结果更新情况较多，考虑取消该缓存
            evict = @CacheEvict(cacheNames = "directoryNameVisible", allEntries = true))
    public Directory updateDirectory(Long directoryId, String name, Long parentId, Boolean visible) {
        Directory directory = cacheService.findById_Directory(directoryId);
        if (directory == null) {
           return null;
        }
        if (Strings.isNotBlank(name)) {
            directory.setName(name);
        }
        if (parentId != null) {
            directory.setParent(parentId);
        }
        if (visible != null) {
            directory.setVisible(visible);
        }
        return directoryRepository.save(directory);
    }
}
