package com.xidian.femts.controller;

import com.xidian.femts.entity.History;
import com.xidian.femts.entity.Manuscript;
import com.xidian.femts.entity.User;
import com.xidian.femts.service.HistoryService;
import com.xidian.femts.service.ManuscriptService;
import com.xidian.femts.service.UserService;
import com.xidian.femts.vo.HistoryRecord;
import com.xidian.femts.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static com.xidian.femts.constants.UserQueryCondition.ID;

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

    private final HistoryService historyService;

    private final UserService userService;

    private final ManuscriptService manuscriptService;

    public HistoryController(HistoryService historyService, UserService userService, ManuscriptService manuscriptService) {
        this.historyService = historyService;
        this.userService = userService;
        this.manuscriptService = manuscriptService;
    }

    /**
     * 查询用户的操作记录
     * @param userId 用户id
     * @return 返回 {@link HistoryRecord} 数组表示所有操作记录，按时间先后排序（首元素时间最近）
     */
    @GetMapping("/user/{userId}")
    public ResultVO findUserHistoriesById(@PathVariable("userId") Long userId) {
        User user = userService.findByCondition(userId.toString(), ID);
        if (user == null) {
            log.warn("[HISTORY] user id is not existed <user_id: {}>", userId);
            return new ResultVO(HttpStatus.BAD_REQUEST, "用户id不存在");
        }
        List<History> histories = historyService.queryUserHistoriesByUserId(userId);
        List<HistoryRecord> records = new ArrayList<>(histories.size());
        if (histories.isEmpty()) {
            return new ResultVO(records);
        }

        String username = user.getUsername();
        // 格式化输出结果
        histories.forEach(history -> {
            Manuscript manuscript = manuscriptService.findById(history.getOptionId());
            if (manuscript == null) {
                log.error("[HISTORY] doc id in history table is not existed <doc_id: {}>",
                        history.getOptionId());
            } else {
                records.add(new HistoryRecord(history, username, manuscript.getTitle()));
            }
        });

        return new ResultVO(records);
    }

    /**
     * 查询某文档的所有操作记录
     * @param docId 文档id
     * @return 返回 {@link HistoryRecord} 数组表示所有操作记录，按时间先后排序（首元素时间最近）
     */
    @GetMapping("/doc/{docId}")
    public ResultVO findDocHistoryById(@PathVariable("docId") Long docId) {
        Manuscript manuscript = manuscriptService.findById(docId);
        if (manuscript == null) {
            log.warn("[HISTORY] doc id is not existed <doc_id: {}>", docId);
            return new ResultVO(HttpStatus.BAD_REQUEST, "文档id不存在");
        }
        List<History> histories = historyService.queryDocHistoriesByOptionId(docId);
        List<HistoryRecord> records = new ArrayList<>(histories.size());
        if (histories.isEmpty()) {
            return new ResultVO(records);
        }

        String title = manuscript.getTitle();
        histories.forEach(history -> {
            User user = userService.findByCondition(history.getUserId().toString(), ID);
            if (user == null) {
                log.error("[HISTORY] user id in history table is not existed <user_id: {}>",
                        history.getUserId());
            } else {
                records.add(new HistoryRecord(history, user.getUsername(), title));
            }
        });
        return new ResultVO(records);
    }
}
