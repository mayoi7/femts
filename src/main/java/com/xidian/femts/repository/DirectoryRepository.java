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

    /**
     * 根据id查询目录名，需要目录可见（目录被用户可见的条件：visible为真，或visible为假但目录为当前用户创建）
     * @param id 目录id
     * @return 当目录可见时，返回目录名，否则返回null
     */
    @Query(value = "SELECT name FROM femts.directory where id = :id " +
            "and (visible = 1 or visible = 0 and created_by = :userId);", nativeQuery = true)
    String findNameByIdIfVisible(@Param("id") Long id, @Param("userId") Long userId);

    /**
     * 查询用户根文件目录
     * @param userId 用户id
     * @return 用户目录的第一级
     */
    @Query(value = "select * from directory where created_by = :userId and parent = 0", nativeQuery = true)
    Directory findSelfDirectory(@Param("userId") Long userId);
}
