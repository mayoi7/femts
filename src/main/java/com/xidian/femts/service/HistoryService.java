package com.xidian.femts.service;


import com.xidian.femts.constants.OptionType;
import com.xidian.femts.entity.History;

import java.util.List;

/**
 * 操作记录服务<br/>
 * 包含操作历史记录与文档读写记录操作，即合并HistoryService和RecordService的功能
 *
 * @author LiuHaonan
 * @date 19:31 2020/1/17
 * @email acerola.orion@foxmail.com
 */
public interface HistoryService {

    /**
     * 查询某用户的所有操作记录
     * @param userId 用户id，不允许为空（在本系统中调用前均已做参数非空校验，即不允许第三方调用）
     * @return 操作记录实体对象列表（列表一定不为空，但里面内容可能为空）
     */
    List<History> queryUserHistoriesByUserId(Long userId);

    /**
     * 查询某文档的所有操作记录
     * @param objectId 文档id，不允许为空（在本系统中调用前均已做参数非空校验，即不允许第三方调用）
     * @return 操作记录实体对象列表（列表一定不为空，但里面内容可能为空）
     */
    List<History> queryDocHistoriesByObjectId(Long objectId);

    /**
     * 添加一条操作记录
     * @param userId 操作人id
     * @param objectId 操作对象
     * @param option 操作类型
     */
    void addOptionHistory(Long userId, Long objectId, OptionType option);
}
