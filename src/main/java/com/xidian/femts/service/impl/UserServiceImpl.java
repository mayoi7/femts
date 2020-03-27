package com.xidian.femts.service.impl;

import com.xidian.femts.constants.UserQueryCondition;
import com.xidian.femts.entity.User;
import com.xidian.femts.repository.UserRepository;
import com.xidian.femts.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author LiuHaonan
 * @date 10:19 2020/1/23
 * @email acerola.orion@foxmail.com
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Cacheable(cacheNames = "user_id", key = "#username")
    public Long findIdByUsername(String username) {
        return userRepository.findIdByUsername(username);
    }

    @Override
    public boolean findDuplicateField(String field, UserQueryCondition condition) {
        switch (condition) {
            case USERNAME:
                return userRepository.findDuplicateUsername(field) != null;
            case PHONE:
                return userRepository.findDuplicatePhone(field) != null;
            case JOBID:
                return userRepository.findByJobId(field) != null;
            case EMAIL:
                return userRepository.findDuplicateEmail(field) != null;
            default:
                return false;
        }
    }

    @Override
    @Cacheable(cacheNames = "username", key = "#userId")
    public String findUsernameById(Long userId) {
        return userRepository.findUsernameById(userId);
    }

    @Override
    @Cacheable(cacheNames = "user", key = "#param + '$' + #condition")
    public User findByCondition(String param, UserQueryCondition condition) {
        if (param == null) {
            log.error("[USER] query param can not be null <query_condition: {}>", condition);
            return null;
        }

        switch (condition) {
            case ID:
                return findById(Long.parseLong(param));
            case USERNAME:
                return findByUsername(param);
            case JOBID:
                return findByJobId(param);
            case PHONE:
                return findByPhone(param);
            case EMAIL:
                return findByEmail(param);
            default:
                return null;
        }
    }

    private User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    private User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    private User findByJobId(String jobId) {
        return userRepository.findByJobId(jobId);
    }

    private User findByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }

    private User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Integer findStateByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            log.warn("[USER] found user is null when get user state <username: {}>", username);
            return null;
        }
        return user.getState().getCode();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CachePut(cacheNames = "user", key = "#user.username + '$USERNAME'")
    public User saveUser(User user) {
        if (user == null) {
            log.warn("[USER] reject save null data");
            return null;
        }
        // 避免恶意修改时间信息
        user.setCreatedAt(null);
        user.setModifiedAt(null);
        return userRepository.saveAndFlush(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CachePut(cacheNames = "user", key = "#user.username + '$USERNAME'")
    public User updateUser(Long userId, User user) {
        // jpa的save会自动触发查询userId是否存在，所以不要做额外的检查操作
        return userRepository.saveAndFlush(user);
    }

    @Override
    public Long countRegistered() {
        return userRepository.count();
    }

    @Override
    public Long countActived() {
        return userRepository.countActivedUser();
    }
}
