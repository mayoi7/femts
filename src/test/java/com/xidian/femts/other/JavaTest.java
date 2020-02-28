package com.xidian.femts.other;

import org.junit.jupiter.api.Test;

import java.io.*;

/**
 * 测试一些java语言特性
 *
 * @author LiuHaonan
 * @date 10:58 2020/2/3
 * @email acerola.orion@foxmail.com
 */
public class JavaTest {

    @Test
    void testObject2String() {
        String a = "aaa";
        if (a instanceof String) {
            System.out.println(a);
        }
    }

    @Test
    void openFile() throws Exception {
        File file = new File("D:/Codes/test/001.pdf");
        BufferedReader reader = null;
        StringBuffer sbf = new StringBuffer();
        reader = new BufferedReader(new FileReader(file));
        String tempStr;
        while ((tempStr = reader.readLine()) != null) {
            sbf.append(tempStr);
        }
        reader.close();
        System.out.println(sbf.toString());
    }

    @Test
    void openFileBinary() throws Exception {
        File file = new File("D:/Codes/test/002.pdf");
        InputStream in = new FileInputStream(file);
        int charInt;
        while((charInt = in.read()) != -1) {
            System.out.print(charInt + " ");
        }
    }

    @Test
    void writeFile() throws Exception {
        BufferedWriter out = new BufferedWriter(new FileWriter("D:/Codes/test/001.zip"));
        out.write("0123456789");
        out.close();
    }
}
