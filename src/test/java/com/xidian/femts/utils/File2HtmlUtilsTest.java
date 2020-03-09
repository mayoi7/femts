package com.xidian.femts.utils;

import org.junit.jupiter.api.Test;

import java.io.File;

/**
 * @author LiuHaonan
 * @date 14:42 2020/2/27
 * @email acerola.orion@foxmail.com
 */
class File2HtmlUtilsTest {

    @Test
    void convertWord2007ToHTML() throws Exception {
        File file = new File("file/temp/007.docx");
//        byte[] bytes = MulFileUtils.changeToBytes(file);
//        System.out.println(Arrays.toString(bytes));
//        File file1 = MulFileUtils.changeBytesToFile(bytes);
//        file1.createNewFile();
//        String content = File2HtmlUtils(file);
//        System.out.println(content);
//        InputStream inputStream = new ByteArrayInputStream(bytes);
//        String content = Word2HtmlUtils.convertWord2007ToHTML(bytes);
//        String content = File2HtmlUtils.convertWord2003ToHTML(file);
//        System.out.println(content);
    }

    @Test
    void testConvertPdfToHTML() throws Exception {
        File file = new File("file/temp/001.pdf");
        byte[] bytes = MulFileUtils.changeFileToBytes(file);
//        String content = File2HtmlUtils.convertPdfToHTML(bytes);
//        System.out.println(content);
    }

}