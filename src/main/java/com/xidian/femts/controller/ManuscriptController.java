package com.xidian.femts.controller;

import com.xidian.femts.constants.UserQueryCondition;
import com.xidian.femts.constants.UserState;
import com.xidian.femts.entity.Directory;
import com.xidian.femts.entity.Manuscript;
import com.xidian.femts.entity.Permission;
import com.xidian.femts.entity.User;
import com.xidian.femts.service.DirectoryService;
import com.xidian.femts.service.ManuscriptService;
import com.xidian.femts.service.PermissionService;
import com.xidian.femts.service.UserService;
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

    private final ManuscriptService manuscriptService;

    private final PermissionService permissionService;

    private final UserService userService;

    private final DirectoryService directoryService;

    public ManuscriptController(ManuscriptService manuscriptService, PermissionService permissionService, UserService userService, DirectoryService directoryService) {
        this.manuscriptService = manuscriptService;
        this.permissionService = permissionService;
        this.userService = userService;
        this.directoryService = directoryService;
    }


    /**
     * 根据文档id查询数据
     * @param manuscriptId 文档id
     * @return 返回code为200时，data为对应数据对象；返回code为400时，data为错误信息
     */
    @GetMapping("/{manuscriptId}")
    public ResultVO returnDocById(@PathVariable("manuscriptId") Long manuscriptId,
                                  @RequestParam(defaultValue = "false") boolean contentRequired) {
        Manuscript manuscript = manuscriptService.findById(manuscriptId);
        if (manuscript == null) {
            return new ResultVO(BAD_REQUEST, "id不存在");
        }
        if (contentRequired) {
            // 如果需要具体的内容部分
        }
        return new ResultVO(manuscript);
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
        Manuscript manuscript = manuscriptService.findById(manuscriptId);

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
     * @return 添加成功返回SUCCESS标记
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
            return ResultVO.SUCCESS;
        }
    }
}


