package com.xidian.femts.repository;

import com.alibaba.fastjson.JSON;
import com.xidian.femts.constants.OptionType;
import com.xidian.femts.entity.History;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author LiuHaonan
 * @date 16:29 2020/1/17
 * @email acerola.orion@foxmail.com
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class HistoryRepositoryTest {

    @Autowired
    private HistoryRepository historyRepository;

    @Test
    public void selectRecord() {
        History history = historyRepository.findHistoryById(2L);
        System.out.println(JSON.toJSONString(history));
    }

    @Test
    @Rollback(value = true)
    public void saveRecord() {
        History history = new History();
        history.setOptionId(1L);
        history.setOptionType(OptionType.UPDATE);
        history.setUserId(2L);
        History saved = historyRepository.save(history);
        System.out.println(saved.toString());
    }
}