package com.xidian.femts.repository;

import com.xidian.femts.entity.History;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author LiuHaonan
 * @date 20:17 2020/1/16
 * @email acerola.orion@foxmail.com
 */
@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {

    /**
     * 主键查询
     * @param id 主键
     * @return 操作记录对象
     */
    History findHistoryById(Long id);

    /**
     * 分页查询某用户下的操作记录（结果按时间倒序排列）
     * @param userId 操作者用户id
     * @param pageable 分页参数
     * @return 操作记录对象列表
     */
    @Query(value = "select * from history where user_id = :userId order by id desc", nativeQuery = true)
    Page<History> listHistoriesByUserIdInPage(Long userId, Pageable pageable);

    /**
     * 分页查询被操作对象（可以是用户或文档，根据type来指定）的记录
     * @param type 操作对象类型，0：用户；1：文档
     * @param objectId 操作对象id
     * @param pageable 分页参数
     * @return 操作记录对象列表
     */
    @Query(value = "select * from history where object_id = :objectId and type = :type order by id desc", nativeQuery = true)
    Page<History> listHistoriesByObjectIdAndTypeInPage(boolean type, Long objectId, Pageable pageable);
}
