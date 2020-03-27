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

    /**
     * 根据用户名查询用户名，即查询是否有重复的用户名
     * @return 查询的结果，如果为空说明没有重复数据
     */
    @Query(value = "select id from user where id = :userId", nativeQuery = true)
    Long findDuplicateUsername(String param);

    /**
     * 根据工号查询工号，即查询是否有重复的工号
     * @return 查询的结果，如果为空说明没有重复数据
     */
    @Query(value = "select id from user where job_id = :param", nativeQuery = true)
    Long findDuplicateJobId(String param);

    /**
     * 根据手机号查询手机号，即查询是否有重复的手机号
     * @return 查询的结果，如果为空说明没有重复数据
     */
    @Query(value = "select id from user where phone = :param", nativeQuery = true)
    Long findDuplicatePhone(String param);

    /**
     * 根据邮箱查询邮箱，即查询是否有重复的邮箱
     * @return 查询的结果，如果为空说明没有重复数据
     */
    @Query(value = "select id from user where email = :param", nativeQuery = true)
    Long findDuplicateEmail(String param);


    @Query(value = "select count(*) from user where state > 1", nativeQuery = true)
    Long countActivedUser();

    User findByUsername(String username);

    User findByJobId(String jobId);

    User findByPhone(String phone);

    User findByEmail(String email);
}
