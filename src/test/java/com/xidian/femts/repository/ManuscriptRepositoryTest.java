package com.xidian.femts.repository;

import com.xidian.femts.vo.SimpleDocInterface;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * @author LiuHaonan
 * @date 18:55 2020/4/7
 * @email acerola.orion@foxmail.com
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
class ManuscriptRepositoryTest {

    @Autowired
    private ManuscriptRepository manuscriptRepository;

    @Test
    void findByTitleLike() {
        // id, title, created_by as creatorId
        List<SimpleDocInterface> manuscripts = manuscriptRepository.findByTitleLike("t");
        manuscripts.forEach(ele -> System.out.println(ele.getCreatorId()));
    }
}