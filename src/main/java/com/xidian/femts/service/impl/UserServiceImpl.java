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
    @Cacheable(cacheNames = "user", key = "#param + '$' + #condition")
    public User findByCondition(String param, UserQueryCondition condition) {
        if (param == null) {
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
    @CachePut(cacheNames = "user", key = "#user.username")
    public User saveUser(User user) {
        if (user == null) {
            log.warn("[USER] reject save null data");
            return null;
        }
        return userRepository.save(user);
    }
}
