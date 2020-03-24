package com.xidian.femts.repository;

import com.xidian.femts.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author LiuHaonan
 * @date 20:17 2020/1/16
 * @email acerola.orion@foxmail.com
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "select id from user where username = :username and state != 0", nativeQuery = true)
    Long findIdByUsername(String username);

    @Query(value = "select username from user where id = :userId", nativeQuery = true)
    String findUsernameById(Long userId);

    @Query(value = "count * from user where state > 1", nativeQuery = true)
    Long countActivedUser();

    User findByUsername(String username);

    User findByJobId(String jobId);

    User findByPhone(String phone);

    User findByEmail(String email);
}
