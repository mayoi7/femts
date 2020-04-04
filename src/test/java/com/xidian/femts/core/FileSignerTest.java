package com.xidian.femts.core;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

/**
 * @author LiuHaonan
 * @date 22:45 2020/2/7
 * @email acerola.orion@foxmail.com
 */
class FileSignerTest {

    FileSigner signer = new FileSigner();

    @Before
    void init() {
//        signer = new FileSigner();
//        signer.init();
    }

    @Test
    void changeToFile() {
    }

    @Test
    void signZipFile() {
    }

    @Test
    void signSingleFile() throws IOException {
        signer.init();
        File file = new File("file/temp/001.pdf");
        byte[] bytes = FileUtils.readFileToByteArray(file);
        FileSigner.FileData fileData = signer.signSingleFile(bytes);
        System.out.println("hash: " + fileData.hash);
        for (int i = 0; i < fileData.bytes.length; i++) {
            System.out.print(fileData.bytes[i] + " ");
        }
    }

    @Test
    void testGetHashFromZipFile() {
        signer.init();
//        String hash = signer.extractHashCodeFromZipFile("file/temp/005.docx");
//        System.out.println("hash=" + hash);
    }

    @Test
    void testGetHashFromSingleFile() {
        signer.init();
        String hash1 = signer.extractHashCodeFromSingleFile(new File("file/temp/001.rtf"));
        String hash2 = signer.extractHashCodeFromSingleFile(new File("file/temp/001.pdf"));
        System.out.println("hash1=" + hash1);
        System.out.println("hash2=" + hash2);
    }
}