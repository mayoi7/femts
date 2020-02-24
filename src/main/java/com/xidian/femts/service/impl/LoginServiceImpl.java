package com.xidian.femts.service.impl;

import com.xidian.femts.entity.Login;
import com.xidian.femts.repository.LoginRepository;
import com.xidian.femts.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author LiuHaonan
 * @date 19:53 2020/1/28
 * @email acerola.orion@foxmail.com
 */
@Service
@Slf4j
public class LoginServiceImpl implements LoginService {

    private final LoginRepository loginRepository;

    public LoginServiceImpl(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    @Override
    public Login saveLoginUser(Long userId, String ip) {
        if (userId == null) {
           log.error("[Login] <add login record> user id is null  <ip: {}>", ip);
           return null;
        }

        Login record = new Login();
        record.setUserId(userId);
        record.setIp(ip);

        return loginRepository.save(record);
    }
}
