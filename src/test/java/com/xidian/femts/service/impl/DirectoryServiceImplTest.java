package com.xidian.femts.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xidian.femts.entity.Directory;
import com.xidian.femts.service.DirectoryService;
import com.xidian.femts.vo.DirectoryElement;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * @author LiuHaonan
 * @date 11:07 2020/3/13
 * @email acerola.orion@foxmail.com
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
class DirectoryServiceImplTest {

    @Autowired
    private DirectoryService directoryService;

    @Test
    void listVisibleDirectories() {
        List<DirectoryElement> list = directoryService.listVisibleDirectories(4L, 4L);
        list.forEach(ele -> {
            System.out.println(JSONObject.toJSONString(ele));
        });
    }

    @Test
    void findById() {
    }

    @Test
    void createEmptyDirectory() {
        Directory directory =
                directoryService.createEmptyDirectory(1L, "test001", 2L, true);
        System.out.println(JSON.toJSONString(directory));
//        directoryService.createEmptyDirectory(2L, "test002", 2L, false);
//        directoryService.createEmptyDirectory(5L, "test003", 2L, true);
    }

    @Test
    void createAndAppendDirectory() {
        directoryService.createAndAppendDirectory(1L, "test001", 2L, true);
        directoryService.createAndAppendDirectory(2L, "test002", 2L, false);
        directoryService.createAndAppendDirectory(5L, "test003", 2L, true);
    }

    @Test
    void appendManuscript() {
        directoryService.appendManuscript(4L, 4L);
        directoryService.appendManuscript(12L, 4L);
    }
}