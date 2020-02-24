package com.xidian.femts.repository;

import com.xidian.femts.entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author LiuHaonan
 * @date 16:58 2020/2/4
 * @email acerola.orion@foxmail.com
 */
@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {
}
