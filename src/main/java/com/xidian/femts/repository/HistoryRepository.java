package com.xidian.femts.repository;

import com.xidian.femts.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

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
     * 查询某用户下的操作记录
     * @param userId 用户id
     * @return 操作记录对象列表
     */
    @Query(value = "select h.id, h.user_id, h.time, h.option_type, h.option_id " +
            "from history h where h.user_id = :userId order by h.id", nativeQuery = true)
    List<History> findHistoriesByUserId(Long userId);

    /**
     * 查询某用户下的操作记录
     * @param optionId 操作对象id
     * @return 操作记录对象列表
     */
    @Query(value = "select h.id, h.user_id, h.time, h.option_type, h.option_id " +
            "from history h where h.option_id = :optionId order by h.id", nativeQuery = true)
    List<History> findHistoriesByOptionId(Long optionId);
}
