package com.xidian.femts.controller;

import com.alibaba.fastjson.JSON;
import com.xidian.femts.constants.*;
import com.xidian.femts.dto.DocumentReq;
import com.xidian.femts.dto.DocumentResp;
import com.xidian.femts.entity.Content;
import com.xidian.femts.entity.Directory;
import com.xidian.femts.entity.Manuscript;
import com.xidian.femts.entity.User;
import com.xidian.femts.service.*;
import com.xidian.femts.utils.TokenUtils;
import com.xidian.femts.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.xidian.femts.constants.UserQueryCondition.ID;
import static com.xidian.femts.constants.UserQueryCondition.USERNAME;
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

    private final UserService userService;

    private final DirectoryService directoryService;

    private final HistoryService historyService;

    private final RedisService redisService;

    public ManuscriptController(ManuscriptService manuscriptService, UserService userService, DirectoryService directoryService, HistoryService historyService, InternalCacheService cacheService, StorageService storageService, RedisService redisService) {
        this.manuscriptService = manuscriptService;
        this.userService = userService;
        this.directoryService = directoryService;
        this.historyService = historyService;
        this.cacheService = cacheService;
        this.storageService = storageService;
        this.redisService = redisService;
    }

    /**
     * 根据文档id查询数据
     * @param id 文档id
     * @param detail 是否返回详细信息，true：是
     * @return 返回code为200时，data为对应数据对象；返回code为400时，data为错误信息
     */
    @GetMapping("/{id}")
    public ResultVO returnDocById(@PathVariable("id") Long id,
                                  @RequestParam(value = "detail", defaultValue = "false") boolean detail) {
        Manuscript manuscript = cacheService.findById_Manuscript(id);
        if (manuscript == null) {
            log.error("[DOC] doc id is not found <doc_id: {}>", id);
            return new ResultVO(BAD_REQUEST, "id不存在");
        }
        String content = cacheService.findContentById_Content(manuscript.getContentId());
        String creator = userService.findUsernameById(manuscript.getCreatedBy());
        String editor = userService.findUsernameById(manuscript.getModifiedBy());

        if (detail) {
            return new ResultVO(new DocumentInfo(manuscript,content, creator, editor));
        } else {
            return new ResultVO(new DocumentResp(manuscript, content, creator, editor));
        }
    }

    /**
     * 创建或更新文档<br/>
     * 不需要传入更新/创建时间参数，以后端数据更新时间为准
     * @param document 文档数据
     * @return 如果创建/更新成功则返回文档信息，否则返回错误信息
     */
    @PostMapping("/post")
    public ResultVO createOrUpdateManuscript(@RequestBody DocumentReq document) {
        Long id = document.getId();
        Manuscript manuscript;
        // 先获取当前登陆的用户，即操作人
        String username = TokenUtils.getLoggedUserInfo();
        User operator = userService.findByCondition(username, USERNAME);

        Manuscript result = manuscriptService.findByTitle(operator.getId(), document.getTitle());
        if (result != null && !result.getId().equals(document.getId())) {
            // 如果重名，则禁止创建或更新（这里的更新指更新标题）
            return new ResultVO(BAD_REQUEST, "已经创建过同名文档，禁止更新/创建");
        }

        Long operatorId = operator.getId();

        if (id == null) {
            manuscript = createFile(document, operatorId);
        } else {
            // 查出待更新文档数据
            Manuscript toUpdate = cacheService.findById_Manuscript(id);
            if (toUpdate == null) {
                log.error("[DOC] doc id not found, will create file <manuscript_id: {}>", id);
                manuscript = createFile(document, operatorId);
            } else {
                // 先校验用户是否有权限更新文档
                if (checkUpdatePermission(toUpdate, operatorId, operator.getLevel())) {
                    manuscript = updateFile(toUpdate, document, operatorId);
                } else {
                    log.error("[DOC] user unauthorized update document <user: {}>", operator);
                    return new ResultVO(BAD_REQUEST, "无权更新");
                }
            }
        }
        if (manuscript == null) {
            log.error("[DOCUMENT] create/update file failed <document: {}>", JSON.toJSONString(document));
            return new ResultVO(BAD_REQUEST, "数据异常");
        } else {
            String creatorName = userService.findUsernameById(manuscript.getCreatedBy());
            String editorName = userService.findUsernameById(manuscript.getModifiedBy());
            DocumentResp resp = new DocumentResp(manuscript, document.getContent(), creatorName, editorName);
            return new ResultVO(resp);
        }
    }

    /**
     * 检查用户是否有权限更新文档<br/>
     * 以下情况才可以进行更新：<br/>
     * 1. 更新者就是文档创建者<br/>
     * 2. 文档设置为公开<br/>
     * 2. 文档设置为仅上级可编辑，且更新人就是操作人<br/>
     * @param id 文档id
     * @return true：可以进行更新
     */
    @GetMapping("/check/edit/{id}")
    public ResultVO checkEditPermission(@PathVariable("id") Long id) {
        String editorName = TokenUtils.getLoggedUserInfo();
        User editor = userService.findByCondition(editorName, USERNAME);
        if (editor == null) {
            log.error("[USER] logged user not found <name: {}>", editorName);
            return new ResultVO(INTERNAL_SERVER_ERROR, "登陆信息异常");
        }

        Manuscript toEdit = cacheService.findById_Manuscript(id);
        if (toEdit == null) {
            log.error("[DOC] doc with such id is not found <id: {}>", id);
            return new ResultVO(BAD_REQUEST, "文档id不存在");
        }
        Long creatorId = toEdit.getCreatedBy();

        // 文档创建人就是编辑人，则无条件授权
        if (creatorId.equals(editor.getId())) {
            return ResultVO.SUCCESS;
        }

        User creator = userService.findByCondition(creatorId.toString(), ID);
        if (creator == null) {
            log.error("[USER] doc creator is not found <doc_id: {}, creator_id: {}>", id, creatorId);
            // 如果创建人为空，则可能是用户已被注销或锁定
            return new ResultVO(INTERNAL_SERVER_ERROR, "文档状态异常");
        }
        Integer editorLevel = editor.getLevel();
        Integer creatorLevel = creator.getLevel();
        if (toEdit.getLevel().canWrite(editorLevel, creatorLevel)) {
            return ResultVO.SUCCESS;
        } else {
            return new ResultVO(BAD_REQUEST, "无权更新");
        }
    }

    /**
     * 检查用户是否有权限更新文档<br/>
     * 以下情况才可以进行更新：<br/>
     * 1. 更新者就是文档创建者<br/>
     * 2. 文档设置为公开<br/>
     * 2. 文档设置为仅上级可编辑，且更新人就是操作人<br/>
     * @param manuscript 文档数据
     * @param editorId 操作人id，禁止为空
     * @param editorLevel 用户人权限级别
     * @return true：可以进行更新
     */
    private boolean checkUpdatePermission(Manuscript manuscript, Long editorId, Integer editorLevel) {
        if (editorId.equals(manuscript.getCreatedBy())) {
            return true;
        }
        switch (manuscript.getLevel()) {
            case CONFIDENTIAL:
                User creator = userService.findByCondition(manuscript.getCreatedBy().toString(), ID);
                return editorLevel > creator.getLevel();
            case PUBLIC:
                return true;
            case UNEDITABLE:
            case PRIVATE:
            default:
                return false;
        }
    }

    /**
     * 创建文件，不需要创建对应的实体文件，也不需要将文件上传到文件系统<br/>
     * 只有当用户点击下载后才触发下载操作
     * @param document 文档数据
     * @param creatorId 创建人用户id
     * @return 返回存储后的文档数据，携带有id信息；如果返回空说明插入失败
     */
    private Manuscript createFile(DocumentReq document, Long creatorId) {
        Long contentId = null;
        if (!StringUtils.isEmpty(document.getContent())) {
            contentId = manuscriptService.saveContent(document.getContent());
        }
        Manuscript toSaved = Manuscript.builder()
                .directoryId(document.getDirectoryId()).type(FileType.CUSTOM)
                .title(document.getTitle()).contentId(contentId)
                .level(document.getLevel()).createdBy(creatorId).modifiedBy(creatorId)
                .build();
        Manuscript manuscript = manuscriptService.saveOrUpdateFile(null, toSaved, null);
        // 在目录表中记录
        Directory directory = directoryService.appendManuscript(document.getDirectoryId(), manuscript.getId());
        if (directory == null) {
            log.error("[DIR] directory append failed <parent_id: {}, doc_id: {}>",
                    document.getDirectoryId(), manuscript.getId());
            return null;
        }
        // 文档总数自增
        redisService.incrementAndGet(RedisKeys.DOCUMENT_COUNT_KEY);

        historyService.addOptionHistory(creatorId, manuscript.getId(),true, Operation.CREATE);
        return manuscript;
    }

    /**
     * 更新文件，会同时更新文件系统中的文件<br/>
     * 实时性较强，不能采用消息队列异步执行
     * @param original 数据库中原始文档数据
     * @param document 新文档数据
     * @param editorId 编辑人用户id
     * @return 返回更新后的文档数据，如果返回空说明更新失败
     */
    private Manuscript updateFile(Manuscript original, DocumentReq document, Long editorId) {
        // 1. 更新数据库中content表
        Content content = null;
        if (original.getContentId() == null) {
            // 说明是新创建文档
            Long contentId = manuscriptService.saveContent(document.getContent());
            original.setContentId(contentId);
            content = new Content(contentId, document.getContent());
        } else {
            content = manuscriptService
                    .updateContent(original.getContentId(), document.getContent());
            if (!content.getId().equals(original.getContentId())) {
                // 如果更新后的内容id不等于更新前的内容id，说明之前文档的内容id为空或是错误的
                log.error("[DOC] doc content id is wrong before <before_content_id: {}, after: {}>",
                        original.getContentId(), content.getId());
                // 目前直接返回空，如果该类情况出现，需要检查是否之前的设计出现了逻辑漏洞，
                // 而不应当掩饰这一错误，而应即使将错误抛出，避免导致更严重的错误
                return null;
            }
        }
        // 2. 修改其他信息
        original.setTitle(document.getTitle());
        original.setLevel(document.getLevel());
        original.setModifiedBy(editorId);
        // TODO: 2020/4/5 目录内容暂不更改
        Manuscript manuscript = manuscriptService.saveOrUpdateFile(original.getId(), original, null);

        // 如果用户没有修改内容就不用更新fastdfs
        if (content.getContent().equals(document.getContent())) {
            // 3. 生成新文件（txt类型，类型在数据库中标记为枚举中的CUSTOM类型）
            //    实际是将String类型转换为字节数据，模拟生成文件
            byte[] bytes = document.getContent().getBytes();
            // 4. 更新文件系统中真实文件，如果文件未上传过就上传，否则就更新
            if (manuscript.getFileId() == null) {
                // 自定义文件类型扩展名为空（可能会导致fastdfs错误，待测试）
                storageService.upload(bytes, FileType.CUSTOM.getName());
            } else {
                // 更新文件id
                String newFileId = storageService.modify(manuscript.getFileId(), bytes, FileType.CUSTOM.getName());
                manuscript.setFileId(newFileId);
                manuscriptService.saveOrUpdateFile(manuscript.getId(), manuscript, null);
            }
        }
        // 5. 添加操作记录
        historyService.addOptionHistory(editorId, manuscript.getId(),true, Operation.UPDATE);
        return manuscript;
    }

    /**
     * 删除文档数据<br/>
     * 并不真正删除文档数据，仅删除目录表中数据，使接口访问不到文档数据即可
     * @param id 待删除的文档id
     * @return 返回删除是否成功的响应
     */
    @DeleteMapping("/{id}")
    public ResultVO deleteManuscript(@PathVariable Long id) {
        Manuscript manuscript = cacheService.findById_Manuscript(id);
        if (manuscript == null) {
            log.error("[DOC] doc to delete is not existed <doc_id: {}>", id);
            return new ResultVO(BAD_REQUEST, "待删除的文档不存在");
        }
        // 获取当前登陆用户，查询是否有权操作
        String username = TokenUtils.getLoggedUserInfo();
        User user = userService.findByCondition(username, USERNAME);
        if (user.getId().equals(manuscript.getCreatedBy()) ||
                user.getState().getCode() >= UserState.ADMIN.getCode()) {
            // 只有当前用户是文档创建者或管理员时，才可以删除该文档
            directoryService.deleteManuscript(id, manuscript.getDirectoryId());

            // 文档数自减
            redisService.decrementAndGet(RedisKeys.DOCUMENT_COUNT_KEY);
            return ResultVO.SUCCESS;
        } else {
            log.error("[DOC] user unauthorized delete document <user: {}>", user);
            return new ResultVO(BAD_REQUEST, "无权删除");
        }
    }

    /**
     * 根据文档标题（和创建者用户名）查询对应的文档内容
     * @param title 文档标题
     * @param creator 文档创建者用户名
     * @return 返回查询到的文档信息
     */
    @GetMapping("/info/name")
    @RequiresRoles("admin")
    public ResultVO findDocumentInfoWithTitle(@RequestParam("title") String title,
                                              @RequestParam("creator") String creator) {
        Long creatorId = userService.findIdByUsername(creator);
        if (creatorId == null) {
            log.error("[DOC] doc creator name is not found <creator: {}>", creator);
            // 大概率是用户自行拼接参数错误，所以不返回500异常
            return new ResultVO(BAD_REQUEST, "文档创建者不存在");
        }
        Manuscript manuscript = manuscriptService.findByTitle(creatorId, title);
        if (manuscript == null) {
            log.error("[DOC] doc with such title and creator is not found <title: {}, creator: {}>",
                    title, creator);
            return new ResultVO(BAD_REQUEST, "该用户未创建过此标题的文档");
        }
        String content = cacheService.findContentById_Content(manuscript.getContentId());
        String editor = userService.findUsernameById(manuscript.getModifiedBy());
        return new ResultVO(new DocumentInfo(manuscript, content, creator, editor));
    }

    /**
     * 检测标题是否可用
     * @param title 标题
     * @param creator 创建人，如果不存在则采用当前登陆用户
     * @return 返回成功则说明信息可用（目前检测的字段仅限标题）
     */
    @GetMapping("detect")
    public ResultVO detectPropsAvailable(@RequestParam(value = "title", required = false) String title,
                                         @RequestParam(value = "creator", required = false) String creator) {
        if (title == null) {
            return new ResultVO(BAD_REQUEST, "标题不能为空");
        }
        if (creator == null) {
            creator = TokenUtils.getLoggedUserInfo();
        }
        Long creatorId = userService.findIdByUsername(creator);
        if (creatorId == null) {
            log.error("[USER] creator not found <creator: {}>", creator);
            return new ResultVO(BAD_REQUEST, "查无用户");
        }
        Manuscript manuscript = manuscriptService.findByTitle(creatorId, title);
        if (manuscript == null) {
            return ResultVO.SUCCESS;
        } else {
            return new ResultVO(BAD_REQUEST, "已存在同名文档");
        }
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
        User user = userService.findByCondition(viewerId.toString(), ID);
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

        switch (manuscript.getLevel()) {
            case PRIVATE:
                return new ResultVO(new ReadWritePermission(false, false));
            case UNEDITABLE:
                return new ResultVO(new ReadWritePermission(true, false));
            case CONFIDENTIAL:
                if (user.getLevel() > user.getLevel()) {
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
            // 注意，根目录id不是0，而是1
            id = 1L;
        }
        String username = TokenUtils.getLoggedUserInfo();
        User user = userService.findByCondition(username, UserQueryCondition.USERNAME);
        if (user == null) {
            log.error("[AUTH] logged user is not found <username: {}>", username);
            return new ResultVO(INTERNAL_SERVER_ERROR, "登陆状态异常");
        }
        List<DirectoryElement> directories = directoryService.listVisibleDirectories(id, user.getId());
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
                                    @RequestParam(value = "visible", required = false) boolean visible) {
        String username = TokenUtils.getLoggedUserInfo();
        User user = userService.findByCondition(username, UserQueryCondition.USERNAME);
        if (user == null) {
            log.error("[USER] logged user not found <username: {}>", username);
            return new ResultVO(INTERNAL_SERVER_ERROR, "登陆状态异常");
        }
        // 返回值用于确认是否保存成功
        Directory directory = directoryService.createAndAppendDirectory(id, name, user.getId(), visible);
        if (directory == null) {
            log.error("[DIR] directory create failed <name: {}, parent_id: {}, username: {}>",
                    name, id, username);
            return new ResultVO(BAD_REQUEST, "保存失败");
        } else {
            return new ResultVO(directory.getId());
        }
    }

    /**
     * 修改文档目录名称、父级目录或权限<br/>
     * 仅目录创建者或管理员才有权修改
     * @param directoryId 目录id
     * @param name 修改后的目录名称
     * @param parentId 修改后的父级目录id
     * @param visible 修改后的文档可视性，true：所有人可见，false：私人可见
     * @return 返回处理结果成功与否的标识
     */
    @PostMapping("/directory/edit/{id}")
    public ResultVO editDirectoryName(@PathVariable("id") Long directoryId,
                                      @RequestParam(value = "name", required = false) String name,
                                      @RequestParam(value = "parent", required = false) Long parentId,
                                      @RequestParam(value = "visible", required = false) Boolean visible) {
        if (name == null && parentId == null && visible == null) {
            return ResultVO.SUCCESS;
        }
        Directory directory = directoryService.updateDirectory(directoryId, name, parentId, visible);
        if (directory == null) {
            // 该方法在service层打印过日志，不需要重复打印
            return new ResultVO(BAD_REQUEST, "目录id不存在");
        }
        return ResultVO.SUCCESS;
    }

    @GetMapping("/list/title")
    public ResultVO fuzzyQueryTitle(@RequestParam("title") String title) {
        if (title.isEmpty()) {
            return ResultVO.SUCCESS;
        }
        List<SimpleDocInterface> interfaces = manuscriptService.fuzzyFindByTitle(title);
        List<SimpleDoc> docs = new ArrayList<>(interfaces.size());
        interfaces.forEach(doc -> {
            String name = userService.findUsernameById(doc.getCreatorId());
            docs.add(new SimpleDoc(doc, name));
        });
        return new ResultVO(docs);
    }
}


