package com.xidian.femts.repository;

import com.xidian.femts.entity.Directory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @author LiuHaonan
 * @date 20:16 2020/1/16
 * @email acerola.orion@foxmail.com
 */
@Repository
public interface DirectoryRepository extends JpaRepository<Directory, Long> {

    Directory getById(Long id);

    @Query(value = "select name from directory where id = :id", nativeQuery = true)
    String findNameById(@Param("id") Long id);
}
