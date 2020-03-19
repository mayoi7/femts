package com.xidian.femts.utils;

import org.junit.jupiter.api.Test;

import java.io.File;

/**
 * @author LiuHaonan
 * @date 14:42 2020/2/27
 * @email acerola.orion@foxmail.com
 */
class FileHtmlConverterTest {

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

    @Test
    void testConvertHTMLtoDocx() {
//        String html = "<div style=\"width:595.0pt;margin-bottom:56.0pt;margin-top:85.0pt;margin-left:85.0pt;margin-right:56.0pt;\"><p style=\"text-align:center;\"><span style=\"font-family:'楷体';font-size:16.0pt;font-weight:bold;\">&#26159;&#30701;&#21457;&#25151;&#39030;&#19978;&#21453;&#20498;&#26159;&#26041;&#24335;</span></p><p>&#20799;&#21834;<span style=\"font-family:'宋体';font-size:16.0pt;\">&#25746;&#26086;</span><span style=\"font-family:'宋体';font-size:16.0pt;font-style:italic;\">&#38463;</span><span style=\"font-style:italic;\">&#26031;&#39039;a</span>F&#21380;s<span style=\"text-decoration:underline;\">d&#38750;</span>dss</p><p>&#25746;<span style=\"font-weight:bold;\">&#26086;&#38463;&#26031;</span>&#39039;adsaa<span id=\"_GoBack\"/></p><p>   &#21714;&#21714;&#21714;</p><p>&#21457;&#29983;&#22823;&#24133;</p></div>";
        String html = "<!doctype html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\"\n" +
                "          content=\"width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0\">\n" +
                "    <meta http-equiv=\"X-UA-Compatible\" content=\"ie=edge\">\n" +
                "    <title>Document</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<div style=\"width:595.0pt;margin-bottom:56.0pt;margin-top:85.0pt;margin-left:85.0pt;margin-right:56.0pt;\"><p style=\"text-align:center;\"><span style=\"font-family:'楷体';font-size:16.0pt;font-weight:bold;\">&#26159;&#30701;&#21457;&#25151;&#39030;&#19978;&#21453;&#20498;&#26159;&#26041;&#24335;</span></p><p>&#20799;&#21834;<span style=\"font-family:'宋体';font-size:16.0pt;\">&#25746;&#26086;</span><span style=\"font-family:'宋体';font-size:16.0pt;font-style:italic;\">&#38463;</span><span style=\"font-style:italic;\">&#26031;&#39039;a</span>F&#21380;s<span style=\"text-decoration:underline;\">d&#38750;</span>dss</p><p>&#25746;<span style=\"font-weight:bold;\">&#26086;&#38463;&#26031;</span>&#39039;adsaa<span id=\"_GoBack\"/></p><p>   &#21714;&#21714;&#21714;</p><p>&#21457;&#29983;&#22823;&#24133;</p></div>\n" +
                "</body>\n" +
                "</html>";
        byte[] bytes = FileHtmlConverter.convertHTMLToWord2007(html);
        File file = MulFileUtils.changeBytesToFile(bytes, true);

//        assert bytes != null;
//        System.out.println(Arrays.toString(bytes));
//        MulFileUtils.changeBytesToFile(bytes, true);
//        assert bytes != null;
//        System.out.println(Arrays.toString(bytes));
    }

}