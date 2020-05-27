package com.xidian.femts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * @author liuhaonan
 */
@SpringBootApplication
@EnableCaching
public class FemtsApplication {

    public static void main(String[] args) {
        SpringApplication.run(FemtsApplication.class, args);
    }

}
