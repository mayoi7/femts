package com.xidian.femts.repository;

import com.xidian.femts.entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author LiuHaonan
 * @date 20:16 2020/1/16
 * @email acerola.orion@foxmail.com
 */
@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {

    @Query(value = "select content from content where id = :id", nativeQuery = true)
    String findTitleById(Long id);
}
