package com.xidian.femts.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
//        List<OperationHistory> operations = historyService.queryOperatorHistories(4L,0);
        int a = 1;
    }
}