package com.xidian.femts.utils;

import com.xidian.femts.constants.HashAlgorithm;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author LiuHaonan
 * @date 22:16 2020/2/6
 * @email acerola.orion@foxmail.com
 */
class HashUtilsTest {

    @Test
    void hashString() {
    }

    @Test
    void hashBytes() throws IOException {
        File f1 = new File("file/001.zip");
        File f2 = new File("file/002.docx");    // 与h1相同的文件
        File f3 = new File("file/003.docx");    // h1文件编辑然后撤销保存后的文件
        File f4 = new File("file/004.docx");    // h1文件修改了内容后的文件

        byte[] b1 = FileUtils.readFileToByteArray(f1);
        byte[] b2 = FileUtils.readFileToByteArray(f2);
        byte[] b3 = FileUtils.readFileToByteArray(f3);
        byte[] b4 = FileUtils.readFileToByteArray(f4);

        String h1 = HashUtils.hashBytes(HashAlgorithm.MD5, b1);
        String h2 = HashUtils.hashBytes(HashAlgorithm.MD5, b2);
        String h3 = HashUtils.hashBytes(HashAlgorithm.MD5, b3);
        String h4 = HashUtils.hashBytes(HashAlgorithm.MD5, b4);

        Assert.assertEquals(h1, h2);
        Assert.assertNotEquals(h1, h3);
        Assert.assertNotEquals(h1, h4);
    }
}