package com.xidian.femts.repository;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author LiuHaonan
 * @date 13:00 2020/2/3
 * @email acerola.orion@foxmail.com
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
class DirectoryRepositoryTest {

    @Autowired
    private DirectoryRepository directoryRepository;

    @Test
    void findNameById() {
        String name = directoryRepository.findNameById(1L);
        System.out.println("name: " + name);
    }
}