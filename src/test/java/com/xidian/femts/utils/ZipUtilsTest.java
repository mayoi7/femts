package com.xidian.femts.utils;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.FileHeader;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author LiuHaonan
 * @date 15:41 2020/2/5
 * @email acerola.orion@foxmail.com
 */
class ZipUtilsTest {

    @Test
    void testAddFileToZip() throws IOException {
        File file = new File("file/abcd001");
        file.mkdir();
//        ZipUtils.addFile("file/001.zip", file);
        file.delete();
    }

    @Test
    void testGetFileFromZip() {
        String hash = ZipUtils.getTargetDirectory("file/temp/001.docx", name -> name.startsWith("$"));
        System.out.println("hash: " + hash);
//        ZipFile file = new ZipFile("file/temp/001.docx");
//        List<FileHeader> files = file.getFileHeaders();
//        files.forEach(ele -> {
//            if (ele.isDirectory()) {
//                System.out.println(ele.getFileName());
//            }
//        });
    }

}