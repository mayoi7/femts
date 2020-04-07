package com.xidian.femts.controller;

import com.xidian.femts.entity.History;
import com.xidian.femts.entity.Manuscript;
import com.xidian.femts.entity.User;
import com.xidian.femts.service.HistoryService;
import com.xidian.femts.service.InternalCacheService;
import com.xidian.femts.service.ManuscriptService;
import com.xidian.femts.service.UserService;
import com.xidian.femts.utils.TokenUtils;
import com.xidian.femts.vo.OperationHistory;
import com.xidian.femts.vo.PageableResultVO;
import com.xidian.femts.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;

import static com.xidian.femts.constants.UserQueryCondition.USERNAME;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * 历史记录（溯源核心）查询控制
 * @author LiuHaonan
 * @date 19:54 2020/2/16
 * @email acerola.orion@foxmail.com
 */
@RequestMapping("/api/1.0/history")
@RestController
//@RequiresRoles("admin")
@Slf4j
public class HistoryController {

    private final InternalCacheService cacheService;

    private final HistoryService historyService;

    private final UserService userService;

    private final ManuscriptService manuscriptService;

    public HistoryController(HistoryService historyService, UserService userService, ManuscriptService manuscriptService, InternalCacheService cacheService) {
        this.historyService = historyService;
        this.userService = userService;
        this.manuscriptService = manuscriptService;
        this.cacheService = cacheService;
    }

    /**
     * 查询用户的操作记录
     * @param username 用户名
     * @return 返回 {@link OperationHistory} 数组表示所有操作记录，按时间先后排序（倒序）
     */
    @GetMapping(value = {"/user/{username}", "/user"})
    public ResultVO findUserHistoriesById(@PathVariable(value = "username", required = false) String username,
                                          @RequestParam(value = "pageNum", defaultValue = "1") @Min(1) int pageNum) {
        if (username == null) {
            // 如果没有传入用户名参数，默认查询当前登陆用户
            username = TokenUtils.getLoggedUserInfo();
        }
        User user = userService.findByCondition(username, USERNAME);
        if (user == null) {
            log.warn("[HISTORY] user id is not existed <username: {}>", username);
            return new ResultVO(BAD_REQUEST, "用户不存在");
        }
        Page<History> histories = historyService.queryOperatorHistories(user.getId(), pageNum - 1);
        List<OperationHistory> records = new ArrayList<>(histories.getSize());
        histories.get().forEach(item -> {
            String name;
            if (item.getType()) {
                // type为true表示操作对象为文档
                name = manuscriptService.findTitleById(item.getObjectId());
            } else {
                // 否则表示操作对象为用户
                name = userService.findUsernameById(item.getObjectId());
            }
            records.add(new OperationHistory(item, name, false));
        });

        return new PageableResultVO(records, histories.getTotalPages());
    }

    /**
     * 查询某文档的所有操作记录
     * @param id 文档id
     * @return 返回 {@link OperationHistory} 数组表示所有操作记录，按时间先后排序（倒序）
     */
    @GetMapping("/doc/{id}")
    public ResultVO findDocHistoryById(@PathVariable("id") Long id,
                                       @RequestParam(value = "pageNum", defaultValue = "1") @Min(1) int pageNum) {
        Manuscript manuscript = cacheService.findById_Manuscript(id);
        if (manuscript == null) {
            log.error("[HISTORY] doc with such id is not found <id: {}>", id);
            return new ResultVO(BAD_REQUEST, "文档不存在");
        }
        Page<History> histories = historyService.queryOperatedObjHistories(true, id, pageNum - 1);
        List<OperationHistory> records = new ArrayList<>(histories.getSize());
        histories.get().forEach(item -> {
            String tempName = userService.findUsernameById(item.getUserId());
            records.add(new OperationHistory(item, tempName, true));
        });
        return new PageableResultVO(records, histories.getTotalPages());
    }
}
