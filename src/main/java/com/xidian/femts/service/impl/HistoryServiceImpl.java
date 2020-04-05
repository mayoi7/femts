package com.xidian.femts.service.impl;

import com.xidian.femts.constants.Operation;
import com.xidian.femts.entity.History;
import com.xidian.femts.repository.HistoryRepository;
import com.xidian.femts.service.HistoryService;
import com.xidian.femts.service.ManuscriptService;
import com.xidian.femts.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author LiuHaonan
 * @date 19:37 2020/1/17
 * @email acerola.orion@foxmail.com
 */
@Service
@Slf4j
public class HistoryServiceImpl implements HistoryService {

    private final HistoryRepository historyRepository;

    private final UserService userService;

    private final ManuscriptService manuscriptService;

    public HistoryServiceImpl(HistoryRepository historyRepository, UserService userService, ManuscriptService manuscriptService) {
        this.historyRepository = historyRepository;
        this.userService = userService;
        this.manuscriptService = manuscriptService;
    }

    @Override
//    @Cacheable(cacheNames = "user_history", key = "#operatorId + '$' + pageNum")
    public Page<History> queryOperatorHistories(Long operatorId, int pageNum) {
        Pageable pageable = PageRequest.of(pageNum, 10);
        return historyRepository.listHistoriesByUserIdInPage(operatorId, pageable);
    }

    @Override
//    @Cacheable(cacheNames = "object_history", key = "#objectId + '$' + isDoc + '$' + pageNum")
    public Page<History> queryOperatedObjHistories(boolean isDoc, Long objectId, int pageNum) {
        Pageable pageable = PageRequest.of(pageNum, 10);
        return historyRepository.listHistoriesByObjectIdAndTypeInPage(isDoc, objectId, pageable);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addOptionHistory(Long userId, Long objectId, boolean type, Operation operation) {
        History history = History.builder()
                .userId(userId).objectId(objectId)
                .type(type).operation(operation)
                .build();
        historyRepository.saveAndFlush(history);
    }
}
