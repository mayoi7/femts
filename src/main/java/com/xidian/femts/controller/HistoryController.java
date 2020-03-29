package com.xidian.femts.controller;

import com.xidian.femts.entity.Manuscript;
import com.xidian.femts.entity.User;
import com.xidian.femts.service.HistoryService;
import com.xidian.femts.service.InternalCacheService;
import com.xidian.femts.service.ManuscriptService;
import com.xidian.femts.service.UserService;
import com.xidian.femts.vo.OperationHistory;
import com.xidian.femts.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.List;

import static com.xidian.femts.constants.UserQueryCondition.USERNAME;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * 历史记录（溯源核心）查询控制
 * @author LiuHaonan
 * @date 19:54 2020/2/16
 * @email acerola.orion@foxmail.com
 */
@RequestMapping("/history")
@RestController
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
    @GetMapping("/user/{username}")
    public ResultVO findUserHistoriesById(@PathVariable("username") String username,
                                          @RequestParam(value = "pageNum", defaultValue = "1") @Min(1) int pageNum) {
        User user = userService.findByCondition(username, USERNAME);
        if (user == null) {
            log.warn("[HISTORY] user id is not existed <username: {}>", username);
            return new ResultVO(BAD_REQUEST, "用户不存在");
        }
        List<OperationHistory> histories = historyService.queryOperatorHistories(user.getId(), pageNum);
        return new ResultVO(histories);
    }

    /**
     * 查询某文档/用户的所有操作记录
     * @param creator 文档创建人的用户名，
     *                因为只有创建人和文档名才能唯一确定文档，如果要查询用户的话，则该项可以为空
     * @param type 查询类型，true：文档；false：用户
     * @param name 文档名或用户名
     * @return 返回 {@link OperationHistory} 数组表示所有操作记录，按时间先后排序（倒序）
     */
    @GetMapping("/doc/{creator}")
    public ResultVO findDocHistoryById(@PathVariable(value = "creator", required = false) String creator,
                                       @RequestParam(value = "type", defaultValue = "true") boolean type,
                                       @RequestParam("name") String name,
                                       @RequestParam(value = "pageNum", defaultValue = "1") @Min(1) int pageNum) {
        if (!type && creator == null) {
            return new ResultVO(BAD_REQUEST, "文档缺少创建人，无法唯一确定文档");
        }
        User user = userService.findByCondition(creator, USERNAME);
        if (user == null) {
            log.error("[HISTORY] file creator is not found <creator_name: {}>", creator);
            return new ResultVO(BAD_REQUEST, "用户不存在");
        }
        Long objectId;
        if (type) {
            // 如果查询的是文档对象
            Manuscript manuscript = manuscriptService.findByTitle(user.getId(), name);
            objectId = manuscript.getId();
        } else {
            objectId = userService.findIdByUsername(name);
        }
        if (objectId == null) {
            log.error("[HISTORY] can not find the data corresponding to id <type: {}, name: {}>",
                    type, name);
            return new ResultVO(BAD_REQUEST, "查询不到参数对应的数据");
        }

        List<OperationHistory> histories = historyService.queryOperatedObjHistories(type, objectId, pageNum);

        return new ResultVO(histories);
    }
}
