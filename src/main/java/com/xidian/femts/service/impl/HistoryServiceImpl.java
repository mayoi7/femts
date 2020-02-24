package com.xidian.femts.service.impl;

import com.xidian.femts.entity.History;
import com.xidian.femts.repository.HistoryRepository;
import com.xidian.femts.service.HistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LiuHaonan
 * @date 19:37 2020/1/17
 * @email acerola.orion@foxmail.com
 */
@Service
@Slf4j
public class HistoryServiceImpl implements HistoryService {

    private final HistoryRepository historyRepository;

    public HistoryServiceImpl(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    @Override
    @Cacheable(cacheNames = "user_history", key = "#userId")
    public List<History> queryUserHistoriesByUserId(Long userId) {
        List<History> histories = historyRepository.findHistoriesByUserId(userId);
        if (histories == null) {
            log.info("[HISTORY] user history records is null <user_id: {}>", userId);
            return new ArrayList<>(1);
        } else {
            return histories;
        }
    }

    @Override
    @Cacheable(cacheNames = "doc_history", key = "#optionId")
    public List<History> queryDocHistoriesByOptionId(Long optionId) {
        List<History> histories = historyRepository.findHistoriesByOptionId(optionId);
        if (histories == null) {
            log.info("[HISTORY] doc history records is null <doc_id: {}>", optionId);
            return new ArrayList<>(1);
        } else {
            return histories;
        }
    }
}
