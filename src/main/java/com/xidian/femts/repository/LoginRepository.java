package com.xidian.femts.repository;

import com.xidian.femts.entity.Login;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author LiuHaonan
 * @date 20:17 2020/1/16
 * @email acerola.orion@foxmail.com
 */
@Repository
public interface LoginRepository extends JpaRepository<Login, Long> {
}
