package com.xidian.femts.repository;

import com.xidian.femts.entity.Manuscript;
import com.xidian.femts.vo.SimpleDocInterface;
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
public interface ManuscriptRepository extends JpaRepository<Manuscript, Long> {

    @Query(value = "select title from manuscript where id = :id", nativeQuery = true)
    String findTitleById(Long id);

    @Query(value = "select * from manuscript where title = :title and created_by = :createdBy", nativeQuery = true)
    Manuscript findByTitleAndAuthor(String title, Long createdBy);

    @Query(value = "select id, title, created_by as creatorId from manuscript where title like :title%", nativeQuery = true)
    List<SimpleDocInterface> findByTitleLike(String title);
}
