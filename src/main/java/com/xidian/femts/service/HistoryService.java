package com.xidian.femts.service;


import com.xidian.femts.constants.Operation;
import com.xidian.femts.vo.OperationHistory;

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
     * @param operatorId 操作人id，不允许为空（在本系统中调用前均已做参数非空校验，即不允许第三方调用）
     * @param pageNum 页号（从0开始）
     * @return 操作记录实体对象列表（列表一定不为空，但里面内容可能为空）
     */
    List<OperationHistory> queryOperatorHistories(Long operatorId, int pageNum);

    /**
     * 查询某文档的所有操作记录
     * @param isDoc 对象是否属于文档，true：是；false：否（属于用户）
     * @param objectId 被操作对象id，不允许为空（在本系统中调用前均已做参数非空校验，即不允许第三方调用）
     * @return 操作记录实体对象列表（列表一定不为空，但里面内容可能为空）
     */
    List<OperationHistory> queryOperatedObjHistories(boolean isDoc, Long objectId, int pageNum);

    /**
     * 添加一条操作记录
     * @param userId 操作人id
     * @param objectId 操作对象
     * @param type 操作类型（true：文档；false：用户）
     * @param operation 操作内容
     */
    void addOptionHistory(Long userId, Long objectId, boolean type, Operation operation);
}
