package com.xidian.femts.service.impl;

import com.xidian.femts.entity.Manuscript;
import com.xidian.femts.repository.ManuscriptRepository;
import com.xidian.femts.service.ManuscriptService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;

/**
 * @author LiuHaonan
 * @date 8:54 2020/2/11
 * @email acerola.orion@foxmail.com
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
class ManuscriptServiceTest {

    @Autowired
    private ManuscriptService manuscriptService;

    @Autowired
    private ManuscriptRepository manuscriptRepository;

    @Test
    void findById() {
    }

    @Test
    void findTitleById() {
    }

    @Test
    void checkIfFileUploaded() {
        File file = new File("file/temp/005.docx");
    }

    @Test
    void saveFile() {
        Manuscript manuscript = manuscriptService.findByTitle(3L, "tf3");
        manuscript.setTitle("tf4");
        manuscriptRepository.save(manuscript);
    }

    @Test
    void updateFile() {
        Manuscript ma = manuscriptRepository.findById(1L).get();
//        ma.setCreatedAt(null);
//        ma.setModifiedAt(null);
        ma.setTitle("ttf1");
        manuscriptRepository.save(ma);
    }
}