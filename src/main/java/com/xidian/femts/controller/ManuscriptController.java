package com.xidian.femts.controller;

import com.xidian.femts.constants.FileType;
import com.xidian.femts.constants.OptionType;
import com.xidian.femts.constants.UserQueryCondition;
import com.xidian.femts.constants.UserState;
import com.xidian.femts.dto.DocumentData;
import com.xidian.femts.entity.Directory;
import com.xidian.femts.entity.Manuscript;
import com.xidian.femts.entity.Permission;
import com.xidian.femts.entity.User;
import com.xidian.femts.service.*;
import com.xidian.femts.utils.TokenUtils;
import com.xidian.femts.vo.DirList;
import com.xidian.femts.vo.ReadWritePermission;
import com.xidian.femts.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * @author LiuHaonan
 * @date 16:53 2020/1/18
 * @email acerola.orion@foxmail.com
 */
@Slf4j
@RestController
@RequestMapping("/api/1.0/doc")
public class ManuscriptController {

    private final InternalCacheService cacheService;

    private final StorageService storageService;

    private final ManuscriptService manuscriptService;

    private final PermissionService permissionService;

    private final UserService userService;

    private final DirectoryService directoryService;

    private final HistoryService historyService;

    public ManuscriptController(ManuscriptService manuscriptService, PermissionService permissionService, UserService userService, DirectoryService directoryService, HistoryService historyService, InternalCacheService cacheService, StorageService storageService) {
        this.manuscriptService = manuscriptService;
        this.permissionService = permissionService;
        this.userService = userService;
        this.directoryService = directoryService;
        this.historyService = historyService;
        this.cacheService = cacheService;
        this.storageService = storageService;
    }

    /**
     * 根据文档id查询数据
     * @param id 文档id
     * @return 返回code为200时，data为对应数据对象；返回code为400时，data为错误信息
     */
    @GetMapping("/{id}")
    public ResultVO returnDocById(@PathVariable("id") Long id,
                                  @RequestParam(defaultValue = "false") boolean contentRequired) {
        Manuscript manuscript = cacheService.findById_Manuscript(id);
        if (manuscript == null) {
            return new ResultVO(BAD_REQUEST, "id不存在");
        }
        if (contentRequired) {
            // 如果需要具体的内容部分
            // TODO: 2020/3/5 添加内容的查找
        }
        return new ResultVO(manuscript);
    }

    /**
     * 创建或更新文档<br/>
     * 不需要传入更新/创建时间参数，以后端数据更新时间为准
     * @param id 文档id，如果无该参数则表明文档是新创建得来，没有id
     * @param document 文档数据
     * @return 如果创建成功则返回SUCCESS，否则返回错误信息
     */
    @PostMapping("/{id}")
    public ResultVO createOrUpdateManuscript(@PathVariable(value = "id", required = false) Long id,
                                             @RequestBody DocumentData document) {
        Manuscript manuscript;
        if (id == null) {
            manuscript = createFile(document);
        } else {
            manuscript = cacheService.findById_Manuscript(id);
            if (manuscript == null) {
                log.error("[DOC] doc id not found, will create file <manuscript_id: {}>", id);
                manuscript = createFile(document);
            } else {
                manuscript = updateFile(id, document);
            }
        }
        return new ResultVO(manuscript);
    }

    /**
     * 创建文件，不需要创建对应的实体文件，也不需要将文件上传到文件系统<br/>
     * 只有当用户点击下载后才触发下载操作
     * @param document 文档数据
     * @return 返回存储后的文档数据，携带有id信息；如果返回空说明插入失败
     */
    private Manuscript createFile(DocumentData document) {
        Long contentId = manuscriptService.saveContent(document.getContent());
        Manuscript manuscript = manuscriptService.saveFile(document.getCreatorId(), document.getDirectoryId(),
                contentId, document.getTitle(), FileType.CUSTOM, null, null, document.getLevel());
        historyService.addOptionHistory(document.getCreatorId(), manuscript.getId(), OptionType.CREATE);
        return manuscript;
    }

    /**
     * 更新文件，会同时更新文件系统中的文件<br/>
     * 实时性较强，不能采用消息队列异步执行
     * @param id 文档id（需要提前确认id存在）
     * @param document 文档数据
     * @return 返回更新后的文档数据，如果返回空说明更新失败
     */
    private Manuscript updateFile(Long id, DocumentData document) {
        // 1. 更新数据库中content表
        Manuscript manuscript = cacheService.findById_Manuscript(id);
        if (id == null) {
            log.error("[DOC] doc id is not found <doc_id: {}>", id);
            return null;
        }
        Long contentId = manuscriptService
                .updateContent(manuscript.getContentId(), document.getContent());
        if (!contentId.equals(manuscript.getContentId())) {
            // 如果更新后的内容id不等于更新前的内容id，说明之前文档的内容id为空或是错误的
            log.error("[DOC] doc content id is wrong before <before_content_id: {}, after: {}>",
                    manuscript.getContentId(), contentId);
            // 目前直接返回空，如果该类情况出现，需要检查是否之前的设计出现了逻辑漏洞，
            // 而不应当掩饰这一错误，而应即使将错误抛出，避免导致更严重的错误
            return null;
        }
        // 2. 生成新文件（txt类型，类型在数据库中标记为枚举中的CUSTOM类型）
        //    实际是将String类型转换为字节数据，模拟生成文件
        byte[] bytes = document.getContent().getBytes();
        // 3. 更新文件系统中真实文件，如果文件未上传过就上传，否则就更新
        if (manuscript.getFileId() == null) {
            // 自定义文件类型扩展名为空（可能会导致fastdfs错误，待测试）
            storageService.upload(bytes, FileType.CUSTOM.getName());
        } else {
            storageService.modify(manuscript.getFileId(), bytes, FileType.CUSTOM.getName());
        }
        // 4. 添加操作记录
        historyService.addOptionHistory(document.getEditorId(), manuscript.getId(), OptionType.UPDATE);
        return manuscript;
    }

    /**
     * 检查用户对某文档的权限
     * <p>
     *     规则：<br/>
     *     1. 未激活用户没有任何权限
     *     2. 被锁定的用户不能获得编辑权
     *     3. 遵循安全级别获取权限
     *     4. 自己可以查询并编辑自己创建的文档，但不得违反上述规则
     * </p>
     * @param viewerId 用户id
     * @param manuscriptId 文档id
     * @return 具体的读写权限许可
     */
    @GetMapping("/permission/check")
    public ResultVO checkPermission(@RequestParam("uid") Long viewerId,
                                    @RequestParam("mid") Long manuscriptId) {
        Manuscript manuscript = cacheService.findById_Manuscript(manuscriptId);

        if (manuscript == null) {
            log.warn("[DOC] id does not exist <id: {}>", manuscriptId);
            return new ResultVO(BAD_REQUEST, "文档id不存在");
        }
        Long creatorId = manuscript.getCreatedBy();
        User user = userService.findByCondition(viewerId.toString(), UserQueryCondition.ID);
        if (user == null) {
            return new ResultVO(BAD_REQUEST, "用户id不存在");
        }
        if (user.getState() == UserState.INACTIVATED) {
            return new ResultVO(new ReadWritePermission(false, false));
        }
        if (creatorId.equals(viewerId)) {
            // 自己查看自己创建的文档
            return new ResultVO(new ReadWritePermission(true, true));
        }

        Permission viewerLevel = permissionService.findById(viewerId);
        if (viewerLevel == null) {
            return new ResultVO(BAD_REQUEST, "文档查询者没有对应的权限级别");
        }
        Permission creatorLevel = permissionService.findById(creatorId);
        if (creatorLevel == null) {
            return new ResultVO(BAD_REQUEST, "文档创建者没有对应的权限级别");
        }

        switch (manuscript.getLevel()) {
            case PRIVATE:
                return new ResultVO(new ReadWritePermission(false, false));
            case UNEDITABLE:
                return new ResultVO(new ReadWritePermission(true, false));
            case CONFIDENTIAL:
                if (viewerLevel.getLevel() < creatorLevel.getLevel()) {
                    // 查询者权限级别更高（同级也不可以）
                    return new ResultVO(new ReadWritePermission(true, true));
                } else {
                    return new ResultVO(new ReadWritePermission(false, false));
                }
            case PUBLIC:
                return new ResultVO(new ReadWritePermission(true, true));
            default:
                log.error("[PERMISSION] appears doc permission that are not recorded " +
                        "<doc_id: {}, permission: {}>", manuscriptId, manuscript.getLevel());
                return new ResultVO(INTERNAL_SERVER_ERROR,"出现了没有被记录的文档权限级别");
        }
    }

    /**
     * 获取当前登陆用户可视空间某目录下的目录结构
     * @param id 目录id，如果为空则获取最上层目录
     * @return 目录结构
     */
    @GetMapping("/directory/list/{id}")
    public ResultVO listVisibleDirectory(@PathVariable(value = "id", required = false) Long id) {
        if (id == null) {
            // 如果id不存在，则赋予根目录id
            id = 0L;
        }
        String username = TokenUtils.getLoggedUserInfo();
        User user = userService.findByCondition(username, UserQueryCondition.USERNAME);
        if (user == null) {
            log.error("[AUTH] logged user is not found <username: {}>", username);
            return new ResultVO(INTERNAL_SERVER_ERROR, "登陆状态异常");
        }
        DirList directories = directoryService.listPublicDirectories(id, user.getId());
        if (directories == null) {
            log.error("[DIR] directory not found <dir_id: {}, user_id: {}>", id, user.getId());
            return new ResultVO(BAD_REQUEST, "目录不存在");
        }
        return new ResultVO(directories);
    }

    /**
     * 在目录下添加新目录
     * @param id 父目录id
     * @param name 目录名称
     * @return 添加成功返回目录id
     */
    @PostMapping("/directory/append/{id}")
    public ResultVO appendDirectory(@PathVariable("id") Long id, @RequestParam("name") String name,
                                    @RequestParam("visible") boolean visible) {
        String username = TokenUtils.getLoggedUserInfo();
        User user = userService.findByCondition(username, UserQueryCondition.USERNAME);
        if (user == null) {
            log.error("[USER] logged user not found <username: {}>", username);
            return new ResultVO(INTERNAL_SERVER_ERROR, "登陆状态异常");
        }
        // 返回值用于确认是否保存成功
        Directory directory = directoryService.createEmptyDirectory(name, id, user.getId(), visible);
        if (directory == null) {
            log.error("[DIR] directory create failed <name: {}, parent_id: {}, username: {}>",
                    name, id, username);
            return new ResultVO(BAD_REQUEST, "保存失败");
        } else {
            return new ResultVO(directory.getId());
        }
    }
}


