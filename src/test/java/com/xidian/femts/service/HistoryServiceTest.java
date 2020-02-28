package com.xidian.femts.service;

import com.xidian.femts.entity.History;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * @author LiuHaonan
 * @date 19:45 2020/1/17
 * @email acerola.orion@foxmail.com
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class HistoryServiceTest {

    @Autowired
    private HistoryService historyService;

    @Test
    public void queryRecords() {
        List<History> histories = historyService.queryUserHistoriesByUserId(2L);
        System.out.println("size1:" + histories.size());
        histories = historyService.queryUserHistoriesByUserId(2L);
        System.out.println("size2: " + histories.size());
        histories.forEach(item -> {
            System.out.println(item.toString());
        });
    }
}