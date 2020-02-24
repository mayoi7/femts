package com.xidian.femts.config;

import com.xidian.femts.factory.StorageFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author LiuHaonan
 * @date 13:00 2020/1/21
 * @email acerola.orion@foxmail.com
 */
@Configuration
public class FastDFSConfig {

    @Bean
    public StorageFactory storageFactory() {
        return new StorageFactory();
    }
}
